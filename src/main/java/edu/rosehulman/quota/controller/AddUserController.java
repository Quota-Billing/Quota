package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.client.SharedServiceClient;
import edu.rosehulman.quota.model.User;
import org.apache.http.HttpException;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddUserController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      String apiKey = request.params(":apiKey");
      String productId = request.params(":productId");
      String userId = request.params(":userId");

      String partnerId = Database.getInstance().getPartnerByApi(apiKey).get().getPartnerId();
      
      User user = new User();
      user.setPartnerId(partnerId);
      user.setProductId(productId);
      user.setUserId(userId);

      Database.getInstance().addUser(user);

      // Send the user to Shared
      boolean sharedRes = SharedServiceClient.getInstance().addUser(partnerId, productId, userId);
      if (!sharedRes) {
        throw new HttpException("Adding user to shared server failed");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    return "";
  }
}
