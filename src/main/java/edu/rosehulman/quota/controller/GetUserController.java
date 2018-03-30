package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.User;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.halt;

import java.util.Optional;

public class GetUserController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");

    Optional<Partner> optPartner = Database.getInstance().getPartnerByApi(apiKey);
    if (!optPartner.isPresent()) {
      throw halt(404, "Missing Partner");
    }

    String partnerId = optPartner.get().getPartnerId();

    Optional<User> optUser = Database.getInstance().getUser(partnerId, productId, userId);

    if (!optUser.isPresent()) {
      throw halt(404, "Missing User");
    }

    return optUser.get().getUserId();
  }
}