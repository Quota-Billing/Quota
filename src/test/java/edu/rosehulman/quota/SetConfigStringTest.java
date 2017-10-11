package edu.rosehulman.quota;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.rosehulman.quota.Database;

// TODO add tiers to all (or just remove this test)
public class SetConfigStringTest {

	
	// note, for the program to work currently, you must have at least an empty products
	@Test
	public void testEmpty() {
		Database db = Database.getInstance();
		db.setConfig("{\r\n" + 
				"			'partnerId':'1',\r\n" + 
				"			'apiKey':'idk',\r\n" + 
				"			'products': [\r\n" + 
				"			]\r\n" + 
				"		}");
		String expected = "Partner: \"1\"";
		String actual = db.getPartner("\"1\"").toString().trim();
		
		assertEquals(expected, actual);
	}
	
	// this one has correct config
	@Test
	public void testSingleList() {
		Database db = Database.getInstance();
		db.setConfig("{\r\n" + 
				"			'partnerId':'1',\r\n" + 
				"			'apiKey':'idk',\r\n" + 
				"			'products': [\r\n" + 
				"				{\r\n" + 
				"					'productId':'2',\r\n" + 
				"					'productName':'pName',\r\n" + 
				"					'quotas': [\r\n" + 
				"						{\r\n" + 
				"							'quotaId':'3',\r\n" +  
				"							'quotaName':'num_searches',\r\n" +  
				"							'type':'numerical_recurring',\r\n" +  
				"							'tiers': [\r\n" + 
				"								{\r\n" + 
				"									'tierId':'4',\r\n" +  
				"									'tierName':'free',\r\n" +  
				"									'max':'1000',\r\n" +  
				"									'price': '0'\r\n" + 				
				"								}\r\n" + 
				"							]\r\n" + 
				"						}\r\n" + 
				"					]\r\n" + 
				"				}\r\n" + 
				"			]\r\n" + 
				"		}");
		String expected = "Partner: \"1\"\n" + "Product: \"2\"\n" + "Quota: \"3\"";
		String actual = db.getPartner("\"1\"").toString().trim();
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMultipleList() {
		Database db = Database.getInstance();
		db.setConfig("{\r\n" + 
				"			'partnerId':'1',\r\n" + 
				"			'apiKey':'idk',\r\n" + 
				"			'products': [\r\n" + 
				"				{\r\n" + 
				"					'productId':'2',\r\n" + 
				"					'quotas': [\r\n" + 
				"						{\r\n" + 
				"							'quotaId':'3'\r\n" +  
				"						},\r\n" + 
				"						{\r\n" + 
				"							'quotaId':'4'\r\n" +  
				"						}\r\n" + 
				"					]\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					'productId':'7',\r\n" + 
				"					'quotas': [\r\n" + 
				"						{\r\n" + 
				"							'quotaId':'99'\r\n" +  
				"						},\r\n" + 
				"						{\r\n" + 
				"							'quotaId':'00'\r\n" +  
				"						}\r\n" + 
				"					]\r\n" + 
				"				}\r\n" + 
				"			]\r\n" + 
				"		}");
		// order not guaranteed
		String actual = db.getPartner("\"1\"").toString().trim();
		
		assertTrue(actual.contains("Partner: \"1\""));
		assertTrue(actual.contains("Product: \"2\""));
		assertTrue(actual.contains("Quota: \"3\""));
		assertTrue(actual.contains("Quota: \"4\""));
		assertTrue(actual.contains("Product: \"7\""));
		assertTrue(actual.contains("Quota: \"99\""));
		assertTrue(actual.contains("Quota: \"00\""));

	}
	
	@Test
	public void testVaried() {
		Database db = Database.getInstance();
		db.setConfig("{\r\n" + 
				"			'partnerId':'1',\r\n" + 
				"			'apiKey':'idk',\r\n" + 
				"			'products': [\r\n" + 
				"				{\r\n" + 
				"					'productId':'9',\r\n" + 
				"					'quotas': [\r\n" + 
				"						{\r\n" + 
				"							'quotaId':'12'\r\n" +  
				"						}\r\n" + 
				"					]\r\n" + 
				"				},\r\n" + 
				"				{\r\n" + 
				"					'productId':'7',\r\n" + 
				"					'quotas': [\r\n" + 
				"						{\r\n" + 
				"							'quotaId':'6'\r\n" +  
				"						},\r\n" + 
				"						{\r\n" + 
				"							'quotaId':'7'\r\n" +  
				"						}\r\n" + 
				"					]\r\n" + 
				"				}\r\n" + 
				"			]\r\n" + 
				"		}");
		// order not guaranteed
		String actual = db.getPartner("\"1\"").toString().trim();
		
		assertTrue(actual.contains("Partner: \"1\""));
		assertTrue(actual.contains("Product: \"9\""));
		assertTrue(actual.contains("Quota: \"12\""));
		assertTrue(actual.contains("Product: \"7\""));
		assertTrue(actual.contains("Quota: \"6\""));
		assertTrue(actual.contains("Quota: \"7\""));

	}
	
	
	

}
