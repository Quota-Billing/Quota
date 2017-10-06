package edu.rosehulman.quota;

import java.util.HashMap;
import java.util.Map;


public class Product {

	private String id;
	// TODO: We need to figure out which of these belong here
	private Map<String, Quota> quotaMap;
	private Map<String, User> userMap;
	
	public Product(String productId) {
		this.id = productId;
		this.quotaMap = new HashMap<>();
		this.userMap = new HashMap<>();
	}

	public void addQuota(Quota quota) {
		this.quotaMap.put(quota.getID(), quota);
	}
	
	public boolean addUser(User user) {
	  if (this.userMap.get(user.getId()) == null) {
	    this.userMap.put(user.getId(), user);
	    return true;
	  }
	  return false;
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
