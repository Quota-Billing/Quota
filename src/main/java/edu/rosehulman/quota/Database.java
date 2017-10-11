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
		Database db = Database.getInstance();
		db.setConfig("{\r\n" + "			'partnerId':'1',\r\n" + "			'apiKey':'idk',\r\n" + "			'products': [\r\n"
		    + "			]\r\n" + "		}");

		/*
		 * getInstance().setConfig("{\r\n" + "			'partnerId':'1',\r\n" +
		 * "			'apiKey':'idk',\r\n" + "			'products': [\r\n" + "				{\r\n" +
		 * "					'productId':'2',\r\n" + "					'quotas': [\r\n" +
		 * "						{\r\n" + "							'quotaId':'3'\r\n" +
		 * "						},\r\n" + "						{\r\n" + "							'quotaId':'4'\r\n"
		 * + "						}\r\n" + "					]\r\n" + "				},\r\n" + "				{\r\n"
		 * + "					'productId':'7',\r\n" + "					'quotas': [\r\n" +
		 * "						{\r\n" + "							'quotaId':'99'\r\n" +
		 * "						},\r\n" + "						{\r\n" +
		 * "							'quotaId':'00'\r\n" + "						}\r\n" + "					]\r\n" +
		 * "				}\r\n" + "			]\r\n" + "		}");
		 */
	}

	/*
	 * POST: /SetConfig Payload: { 'partnerId':'1', 'apiKey':'', 'products': [ {
	 * 'productId':'' 'quotas': [ { 'quotaId':'', // tier info here } ] } ] }
	 */

	public void setConfig(String body) {
		// from
		// https://stackoverflow.com/questions/5490789/json-parsing-using-gson-for-java
		JsonElement jelement = new JsonParser().parse(body);
		JsonObject jobject = jelement.getAsJsonObject();
		String partnerId = jobject.get("partnerId").toString();
		Partner configPartner = new Partner(partnerId);

		String apiKey = jobject.get("apiKey").toString();
		configPartner.setApiKey(apiKey);
		JsonArray productArray = jobject.getAsJsonArray("products");

		JsonObject product;
		for (int i = 0; i < productArray.size(); i++) {
			product = productArray.get(i).getAsJsonObject();
			Product currProduct = new Product(product.get("productId").toString(), product.get("productName").toString());
			JsonArray quotaArray = product.getAsJsonArray("quotas");
			for (int j = 0; j < quotaArray.size(); j++) {
				JsonObject quota = quotaArray.get(j).getAsJsonObject();
				Quota currQuota = new Quota(quota.get("quotaId").toString(), quota.get("quotaName").toString());
				JsonArray tierArray = quota.getAsJsonArray("tiers");
				for (int k = 0; k < tierArray.size(); k++) {
					JsonObject tier = tierArray.get(k).getAsJsonObject();
					currQuota.addTier(new Tier(tier.get("id").toString(), tier.get("name").toString(),
					    tier.get("max").getAsDouble(), tier.get("price").getAsDouble()));
				}
				currProduct.addQuota(currQuota);
			}
			configPartner.addProduct(currProduct);
		}

		// TODO what if overwriting?
		partnerMap.put(partnerId, configPartner);

		System.out.println(configPartner.toString());

		/*
		 * jobject = jobject.getAsJsonObject("data"); JsonArray jarray =
		 * jobject.getAsJsonArray("translations"); jobject =
		 * jarray.get(0).getAsJsonObject(); String result =
		 * jobject.get("translatedText").toString();
		 */
	}

	public Partner getPartner(String id) {
		return this.partnerMap.get(id);
	}

	// TODO fix inconsistencies with quota. Is a user needed?
	public Quota getQuota(String partnerId, String productId, String userId, String quotaId) {
		return null;
		// return
		// partnerMap.get(partnerId).getProduct(productId).getUser(userId).getQuota(quotaId);
	}

	public boolean addUser(String partnerId, String productId, String userId) {
		return this.partnerMap.get(partnerId).getProduct(productId).addUser(new User(userId));
	}

}
