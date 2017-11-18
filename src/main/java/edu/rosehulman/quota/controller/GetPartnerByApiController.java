package edu.rosehulman.quota.controller;

import java.util.Optional;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.model.Partner;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetPartnerByApiController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");

    Optional<Partner> optPartner = Database.getInstance().getPartnerByApi(apiKey);
    if (!optPartner.isPresent()) {
      response.status(404);
      return "";
    }

    return optPartner.get().getPartnerId();
  }

}
