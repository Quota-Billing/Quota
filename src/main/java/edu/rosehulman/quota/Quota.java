package edu.rosehulman.quota;

public class Quota {

	
	private String id;
	
	public Quota(String quotaId) {
		this.id = quotaId;
	}

	public String getID() {
		return this.id;
	}
	
	
	@Override
	public String toString() {
		String toReturn = "Quota: "  + id + "\n";
		// tiers?
		/*for(String id : this.quotaMap.keySet()) {
			toReturn += this.quotaMap.get(id).toString();
		}*/
		return toReturn;
	}

}
