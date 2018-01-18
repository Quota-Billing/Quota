package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.model.Partner;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Optional;

import static spark.Spark.halt;

public class GetPartnerByApiController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");

    Optional<Partner> optPartner = Database.getInstance().getPartnerByApi(apiKey);
    if (!optPartner.isPresent()) {
      throw halt(404);
    }

    return optPartner.get().getPartnerId();
  }
}
