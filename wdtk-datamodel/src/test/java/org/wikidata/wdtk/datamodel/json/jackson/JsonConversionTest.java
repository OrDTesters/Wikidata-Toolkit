package org.wikidata.wdtk.datamodel.json.jackson;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;





import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.EntityId;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.EntityIdValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.GlobeCoordinate;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.GlobeCoordinateValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.Quantity;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.QuantityValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.StringValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.Time;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.TimeValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.ValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.ItemIdImpl;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.PropertyIdImpl;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.NoValueSnakImpl;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.SomeValueSnakImpl;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.ValueSnakImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a superclass for all tests regarding the conversion of Wikidata
 * Web-API JSON into the WDTK data model and the other way around.
 * It provides mainly constants and the needed mapper objects.
 * 
 * @author Fredo Erxleben
 *
 */
public abstract class JsonConversionTest {
	
	// TODO maybe decompose the time a bit to have less magic strings in it
	
	protected ObjectMapper mapper = new ObjectMapper();
	protected Logger logger = Logger.getLogger(JsonConversionTest.class);
	
	protected static final String entityTypeItem = "item";
	
	// the id's used in the tests
	protected static final String propertyId = "P1";
	protected static final String itemId = "Q1";
	protected static final int numericId = 1;
	protected static final String statementId = "statement_foobar";
	protected static final String rankNormal = "normal";
	protected static final String rankDeprecated = "deprecated";
	protected static final String rankPreferred = "preferred";
	
	// stand-alone descriptions of Value-parts
	protected static final String stringValueJson = "{\"type\":\"" + ValueImpl.typeString + "\",\"value\":\"foobar\"}";
	protected static final String entityIdValueJson = "{\"type\":\"" + ValueImpl.typeEntity + "\",\"value\":{\"entity-type\":\"" + entityTypeItem + "\",\"numeric-id\":" + numericId + "}}";
	protected static final String timeValueJson = "{\"type\":\"" + ValueImpl.typeTime + "\", \"value\":{\"time\":\"+00000002013-10-28T00:00:00Z\",\"timezone\":0,\"before\":0,\"after\":0,\"precision\":11,\"calendarmodel\":\"http://www.wikidata.org/entity/Q1985727\"}}";
	protected static final String globeCoordinateValueJson = "{\"type\":\"" + ValueImpl.typeCoordinate + "\", \"value\":{\"latitude\":-90,\"longitude\":0,\"precision\":10,\"globe\":\"http://www.wikidata.org/entity/Q2\"}}";
	protected static final String quantityValueJson = "{\"type\":\"" + ValueImpl.typeQuantity + "\",\"value\":{\"amount\":\"+1\",\"unit\":\"1\",\"upperBound\":\"+1.5\",\"lowerBound\":\"-0.5\"}}";
	
	// stand-alone descriptions of ItemDocument-parts
	protected static final String itemTypeJson = "\"type\":\"item\"";
	protected static final String mltvJson = "{\"language\": \"en\", \"value\": \"foobar\"}";
	protected static final String siteLinkJson = "{\"site\":\"enwiki\", \"title\":\"foobar\", \"badges\":[]}";
	protected static final String noValueSnakJson = "{\"snaktype\":\"novalue\",\"property\":\"" + propertyId + "\"}";
	protected static final String someValueSnakJson = "{\"snaktype\":\"somevalue\",\"property\":\"" + propertyId + "\"}";
	protected static final String commonsValueSnakJson = "{\"snaktype\":\"value\",\"property\":\"" + propertyId + "\",\"datatype\":\"" + ValueSnakImpl.datatypeCommons + "\",\"datavalue\":" + stringValueJson +"}";

	// wrapping into item document structure for dedicated tests
	protected static final String wrappedLabelJson = "{\"labels\":{\"en\":" + mltvJson + "}," + itemTypeJson + "}";
	protected static final String wrappedDescriptionJson = "{\"descriptions\":{\"en\":" + mltvJson + "}," + itemTypeJson + "}";
	protected static final String wrappedAliasJson = "{ \"aliases\":{\"en\":[" + mltvJson + "]}," + itemTypeJson + "}";
	protected static final String wrappedItemIdJson = "{\"id\":\"" + itemId + "\"," + itemTypeJson + "}";
	protected static final String wrappedSiteLinkJson = "{\"sitelinks\":{\"enwiki\":" + siteLinkJson + "}," + itemTypeJson + "}";

	protected static final String emptyStatementJson = "{\"type\":\"statement\",\"id\":\"" + statementId + "\",\"rank\":\"" + rankNormal + "\",\"mainsnak\":" + noValueSnakJson + "}";
	
	// objects to test against
	// should (of course) correspond to the JSON strings counterpart
	protected static final MonolingualTextValueImpl testMltv = new MonolingualTextValueImpl("en", "foobar");
	protected static final SiteLinkImpl testSiteLink = new SiteLinkImpl("enwiki", "foobar");

	protected static final StringValueImpl testStringValue = new StringValueImpl("foobar");
	protected static final EntityIdValueImpl testEntityIdValue = new EntityIdValueImpl(new EntityId(entityTypeItem, numericId));
	protected static final TimeValueImpl testTimeValue = new TimeValueImpl(new Time("+00000002013-10-28T00:00:00Z",0,0,0,11, "http://www.wikidata.org/entity/Q1985727"));
	protected static final GlobeCoordinateValueImpl testGlobeCoordinateValue = new GlobeCoordinateValueImpl(new GlobeCoordinate(-90, 0, 10, "http://www.wikidata.org/entity/Q2"));
	protected static final QuantityValueImpl testQuantityValue = new QuantityValueImpl(new Quantity(new BigDecimal(1), new BigDecimal(1.5), new BigDecimal(-0.5)));

	protected static final NoValueSnakImpl testNoValueSnak = new NoValueSnakImpl(propertyId);
	protected static final SomeValueSnakImpl testSomeValueSnak = new SomeValueSnakImpl(propertyId);
	protected static final ValueSnakImpl testCommonsValueSnak = new ValueSnakImpl(propertyId, ValueSnakImpl.datatypeCommons, testStringValue);
	// TODO continue testing using stringValueSnak, timeValueSnak, globeCoordinateValueSnak	
	
	
	
	// puzzle pieces for creation of the test of ItemDocument and PropertyDocument
	protected Map<String, MonolingualTextValueImpl> testMltvMap;
	protected Map<String, List<MonolingualTextValueImpl>> testAliases;
	protected ItemIdImpl testItemId;
	protected PropertyIdImpl testPropertyId;
	protected Map<String, SiteLinkImpl> testSiteLinkMap;
	protected StatementImpl testEmptyStatement;
	protected ClaimImpl testClaim;
	
	@Before
	public void setupTestMltv(){
		testMltvMap = new HashMap<>();
		testMltvMap.put("en", TestMonolingualTextValue.testMltv);
	}

	@Before
	public void setupTestAliases(){
		testAliases = new HashMap<>();
		List<MonolingualTextValueImpl> aliases = new LinkedList<>();
		aliases.add(TestMonolingualTextValue.testMltv);
		testAliases.put("en", aliases);
	}
	
	@Before
	public void setupTestItemId(){
		testItemId = new ItemIdImpl(itemId);
		assertEquals(testItemId.getId(), itemId);
	}
	
	@Before
	public void setupTestPropertyId(){
		testPropertyId = new PropertyIdImpl(propertyId);
		assertEquals(testPropertyId.getId(), propertyId);
	}
	
	@Before
	public void setupTestSiteLinks(){
		testSiteLinkMap = new HashMap<>();
		testSiteLinkMap.put("enwiki", TestSiteLink.testSiteLink);
	}
	
	@Before
	public void setupTestStatementAndClaim(){
		testEmptyStatement = new StatementImpl(statementId, testNoValueSnak);
		testClaim = new ClaimImpl(testEmptyStatement, new PropertyIdImpl(propertyId));
		testEmptyStatement.setClaim(testClaim);
	}
	
	@Before
	public void configureLogging() {
		// Create the appender that will write log messages to the console.
		ConsoleAppender consoleAppender = new ConsoleAppender();
		// Define the pattern of log messages.
		// Insert the string "%c{1}:%L" to also show class name and line.
		String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
		consoleAppender.setLayout(new PatternLayout(pattern));
		// Change to Level.ERROR for fewer messages:
		consoleAppender.setThreshold(Level.INFO);

		consoleAppender.activateOptions();
		Logger.getRootLogger().addAppender(consoleAppender);
	}
	
}
