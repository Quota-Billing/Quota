package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.Logging;
import edu.rosehulman.quota.client.SharedServiceClient;
import edu.rosehulman.quota.model.Partner;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.halt;

import java.util.Optional;

public class DeleteUserController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");

    Optional<Partner> optPartner = Database.getInstance().getPartnerByApi(apiKey);
    if (!optPartner.isPresent()) {
      throw halt(404, "Partner not present");
    }
    String partnerId = optPartner.get().getPartnerId();
    
    // delete the given user
    if (!Database.getInstance().deleteUser(partnerId, productId, userId)) {
      throw halt(404, "Deleting user in database failed");
    }

//     Send the delete message to Shared
    boolean sharedRes = SharedServiceClient.getInstance().deleteUser(partnerId, productId, userId);
    if (!sharedRes) {
      Logging.errorLog("Deleting user in shared server failed");
      throw halt(500);
    }

    return "";
  }
}
