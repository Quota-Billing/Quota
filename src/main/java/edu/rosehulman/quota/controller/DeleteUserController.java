package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.Logging;
import edu.rosehulman.quota.client.SharedServiceClient;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.halt;

public class DeleteUserController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");

    String partnerId = Database.getInstance().getPartnerByApi(apiKey).get().getPartnerId();

    // delete the given user
    if (!Database.getInstance().deleteUser(partnerId, productId, userId)) {
      throw halt(404);
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
