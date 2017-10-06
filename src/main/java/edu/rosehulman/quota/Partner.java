package edu.rosehulman.quota;

import java.util.HashMap;

public class Partner {

	private String id;
	private HashMap<String, Product> productMap;
	
	public Partner(String partnerId) {
		this.id = partnerId;
		this.productMap = new HashMap<>();
	}

	public  void addProduct(Product currProduct) {
		this.productMap.put(currProduct.getId(), currProduct);
	}
	
	// for testing 
	@Override
	public String toString() {
		String toReturn = "Partner: "  + id + "\n";
		for(String id : this.productMap.keySet()) {
			toReturn += this.productMap.get(id).toString();
		}
		return toReturn;
		
	}
	
	public Product getProduct(String productId) {
		return this.productMap.get(productId);
	}
	
	
}
