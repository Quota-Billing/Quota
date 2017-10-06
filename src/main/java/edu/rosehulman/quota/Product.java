package edu.rosehulman.quota;

import java.util.HashMap;


public class Product {

	private String id;
	private HashMap<String, Quota> quotaMap;
	
	public Product(String productId) {
		this.id = productId;
		this.quotaMap = new HashMap<>();
	}

	public void addQuota(Quota quota) {
		this.quotaMap.put(quota.getID(), quota);
	}

	public String getId() {
		return this.id;
	}

	// for testing 
	@Override
	public String toString() {
		String toReturn = "Product: "  + id + "\n";
		for(String id : this.quotaMap.keySet()) {
			toReturn += this.quotaMap.get(id).toString();
		}
		return toReturn;
	}

	/*public User getUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}*/
	
}
