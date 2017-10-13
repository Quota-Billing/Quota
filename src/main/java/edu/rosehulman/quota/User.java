package edu.rosehulman.quota;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {

  private String id;
  // Remove once we have a real db:
  private Map<String, Quota> quotaMap;

  public User(String user_id, Product product) {
    this.id = user_id;
    Map<String, Quota> possibleQuotas = product.getQuotas();
    for (String key : possibleQuotas.keySet()) {
      Quota productQuota = possibleQuotas.get(key);
      List<Tier> productTiers = productQuota.getTiers();

      Quota userQuota = new Quota(key);
      List<Tier> userTiers = new ArrayList<Tier>();

      for (int i = 0; i < productTiers.size(); i++) {
        if (i == 0) {
          // Default is first tier in list?
          userQuota.setActiveTier(productTiers.get(i).getId());
        }
        userTiers.add(new Tier(productTiers.get(i)));
      }
      userQuota.setTiers(userTiers);
      quotaMap.put(key, userQuota);
    }
  }

  public String getId() {
    return this.id;
  }
}
