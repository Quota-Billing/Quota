package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetUserController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");

    String partnerId = Database.getInstance().getPartnerByApi(apiKey).get().getPartnerId();

    if (!Database.getInstance().getUser(partnerId, productId, userId).isPresent()) {
      response.status(404);
    }

    return "";
  }
}