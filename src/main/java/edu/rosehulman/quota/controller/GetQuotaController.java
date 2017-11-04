package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetQuotaController implements Route {
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String partnerId = request.params(":partnerId");
    String productId = request.params(":productId");
    String quotaId = request.params(":quotaId");

    if (!Database.getInstance().getQuota(partnerId, productId, quotaId).isPresent()) {
      response.status(404);
    }

    return "";
  }
}
