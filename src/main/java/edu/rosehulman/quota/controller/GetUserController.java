package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetUserController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String partnerId = request.params(":partnerId");
    String productId = request.params(":productId");
    String userId = request.params(":userId");

    if (!Database.getInstance().getUser(partnerId, productId, userId).isPresent()) {
      response.status(404);
    }

    return "";
  }
}
