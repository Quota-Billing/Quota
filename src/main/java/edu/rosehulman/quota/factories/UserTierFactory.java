package edu.rosehulman.quota.factories;

import edu.rosehulman.quota.model.UserTier;

public class UserTierFactory {

  public UserTier createUserTier(String partnerId, String productId, String quotaId, String tierId, String userId, String value) {

    UserTier newUserTier = new UserTier();
    newUserTier.setPartnerId(partnerId);
    newUserTier.setProductId(productId);
    newUserTier.setQuotaId(quotaId);
    newUserTier.setTierId(tierId);
    newUserTier.setUserId(userId);
    newUserTier.setValue(value);
    
    return newUserTier;
  }

}
