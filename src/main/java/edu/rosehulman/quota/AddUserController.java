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

    try {
      // Add the user to our database
      Database.getInstance().addUser(partnerId, productId, userId);
    } catch(Exception e) {
      response.status(500);
      return response;
    }

    // Send the user to Shared
//    Response sharedRes = SharedServiceClient.getInstance().addUser(partnerId, productId, userId);

    return response; // Change to sharedRes once implemented
  }
}
