package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.client.SharedServiceClient;
import edu.rosehulman.quota.model.User;
import org.apache.http.HttpException;
import spark.Request;
import spark.Response;
import spark.Route;

public class DeleteUserController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String partnerId = request.params(":partnerId");
    String productId = request.params(":productId");
    String userId = request.params(":userId");


    // delete the given user
    if(!Database.getInstance().deleteUser(partnerId, productId, userId)) {
      response.status(404);
    }
    
    
    // Send the delete message to Shared
    boolean sharedRes = SharedServiceClient.getInstance().deleteUser(partnerId, productId, userId);
    if (!sharedRes) {
      throw new HttpException("Deleting user in shared server failed");
    }

    return "";
  }
}
