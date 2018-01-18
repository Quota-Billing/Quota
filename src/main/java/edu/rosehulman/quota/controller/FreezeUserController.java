package edu.rosehulman.quota.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.model.User;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Optional;

import static spark.Spark.halt;

public class FreezeUserController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");

    String partnerId = Database.getInstance().getPartnerByApi(apiKey).get().getPartnerId();

    Optional<User> userOptional = Database.getInstance().getUser(partnerId, productId, userId);

    if (!userOptional.isPresent()) {
      throw halt(404);
    }
    User user = userOptional.get();

    // assume that we are freezing user
    boolean freeze = true;
    if (!request.body().isEmpty()) {
      JsonObject jsonObject = new JsonParser().parse(request.body()).getAsJsonObject();
      freeze = jsonObject.get("freeze").getAsBoolean();
    }
    user.setFrozen(freeze);
    boolean updated = Database.getInstance().updateUser(user);

    if (!updated) {
      throw halt(500);
    }

    return "";
  }
}
