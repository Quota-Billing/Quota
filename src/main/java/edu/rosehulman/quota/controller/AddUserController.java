package edu.rosehulman.quota.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.Logging;
import edu.rosehulman.quota.client.SharedServiceClient;
import edu.rosehulman.quota.factories.UserFactory;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.User;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.halt;

import java.util.Optional;

public class AddUserController implements Route {
  
  private UserFactory factory;

  public AddUserController(UserFactory factory) {
    this.factory = factory;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");

    Optional<Partner> optPartner = Database.getInstance().getPartnerByApi(apiKey);
    if (!optPartner.isPresent()) {
      throw halt(404, "Partner not present");
    }
    String partnerId = optPartner.get().getPartnerId();
    
    JsonObject userJsonObject = new JsonParser().parse(request.body()).getAsJsonObject();
    String userId = userJsonObject.get("id").getAsString();

    User user = factory.createUser(partnerId, productId, userId);

    Database.getInstance().addUser(user);

    // Send the user to Shared
    boolean sharedRes = SharedServiceClient.getInstance().addUser(partnerId, productId, userId);
    if (!sharedRes) {
      Logging.errorLog("Adding user to shared server failed");
      throw halt(500);
    }
    return "";
  }
}
