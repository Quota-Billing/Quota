package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetPartnerByApiController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");

    if (!Database.getInstance().getPartnerByApi(apiKey).isPresent()) {
      response.status(404);
    }

    return "";
  }

}
