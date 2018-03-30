package edu.rosehulman.quota.factories;

import edu.rosehulman.quota.model.User;

public class UserFactory {
  
  public User createUser(String partnerId, String productId, String userId) {
    User user = new User();
    user.setPartnerId(partnerId);
    user.setProductId(productId);
    user.setUserId(userId);
    return user;
  }

}
