package edu.rosehulman.quota;

import spark.Request;
import spark.Response;
import spark.Route;

public class AddUserController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String partnerId = request.params(":partnerId");
    String productId = request.params(":productId");
    String userId = request.params(":userId");

    // Add the user to our database
    Database.getInstance().addUser(partnerId, productId, userId);

    // Send the user to Shared
//    SharedServiceClient.getInstance().addUser(partnerId, productId, userId);

    // Return to caller a success
    return "successfully added " + userId;
  }
}
