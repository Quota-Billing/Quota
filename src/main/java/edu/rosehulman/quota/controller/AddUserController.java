package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.client.SharedServiceClient;
import edu.rosehulman.quota.model.User;
import org.apache.http.HttpException;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddUserController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String partnerId = request.params(":partnerId");
    String productId = request.params(":productId");
    String userId = request.params(":userId");

    User user = new User();
    user.setPartnerId(partnerId);
    user.setProductId(productId);
    user.setUserId(userId);

    // TODO: Add the user to our database
    // Database.getInstance().addUser(user);

    // Send the user to Shared
    boolean sharedRes = SharedServiceClient.getInstance().addUser(partnerId, productId, userId);
    if (!sharedRes) {
      throw new HttpException("Adding user to shared server failed");
    }

    return response;
  }
}
