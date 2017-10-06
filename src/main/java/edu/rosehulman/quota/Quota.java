package edu.rosehulman.quota;

import java.util.List;

public class Quota {
	private String id;
	private String name;
	private String type; // TODO: Maybe change this to an enum or use the strategy pattern
	private List<Tier> tiers;
		
	public Quota(String quotaId) {
		this.id = quotaId;
	}

	public Quota() {
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


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<Tier> getTiers() {
    return tiers;
  }

  public void setTiers(List<Tier> tiers) {
    this.tiers = tiers;
  }
}
