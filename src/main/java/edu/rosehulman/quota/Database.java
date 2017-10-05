package edu.rosehulman.quota;

import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Database {

	private static Database instance;
	private HashMap<String, Partner> partnerMap;
	
	private Database() {
		partnerMap = new HashMap<>();
	}
	
	public static synchronized Database getInstance() {
		    if (instance == null) {
		      instance = new Database();
		    }
		    return instance;
	}
	
	// testing
	public static void main(String args[]) {
		getInstance().setConfig("{\r\n" + 
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
	}

	/*
	 * POST: /SetConfig
		Payload:
		{
			'partnerId':'1',
			'apiKey':'',
			'products': [
				{
					'productId':''
					'quotas': [
						{
							'quotaId':'',
							// tier info here
						}
					]
				}
			]
		}
	 */

	
	
	
	public void setConfig(String body) {
		// from https://stackoverflow.com/questions/5490789/json-parsing-using-gson-for-java
	    JsonElement jelement = new JsonParser().parse(body);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    String partnerId = jobject.get("partnerId").toString();
	    Partner configPartner = new Partner(partnerId);
	    
	    // TODO what to do with this?
	    String apiKey = jobject.get("apiKey").toString();
	    JsonArray productArray = jobject.getAsJsonArray("products");
	    
	    
	    JsonObject product;
	    for(int i = 0; i < productArray.size(); i++) {
	    	product = productArray.get(i).getAsJsonObject();
	    	Product currProduct = new Product(product.get("productId").toString());
	    	JsonArray quotaArray = product.getAsJsonArray("quotas");
	    	for(int j = 0; j < quotaArray.size(); j++) {
	    		currProduct.addQuota(new Quota(quotaArray.get(j).getAsJsonObject().get("quotaId").toString()));
	    	}
	    	configPartner.addProduct(currProduct);
	    }
	    
	    System.out.println(configPartner.toString());
	    
	    /*
	    jobject = jobject.getAsJsonObject("data");
	    JsonArray jarray = jobject.getAsJsonArray("translations");
	    jobject = jarray.get(0).getAsJsonObject();
	    String result = jobject.get("translatedText").toString();*/
	}
}
