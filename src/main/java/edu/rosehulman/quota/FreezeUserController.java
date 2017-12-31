package edu.rosehulman.quota;

import static spark.Spark.halt;

import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.rosehulman.quota.model.User;
import spark.Request;
import spark.Response;
import spark.Route;

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
    boolean freezeUser = true;
    if (!request.body().isEmpty()) {
      JsonObject jsonObject = new JsonParser().parse(request.body()).getAsJsonObject();
      freezeUser = jsonObject.get("freezeUser").getAsBoolean();
    }
    user.setFrozen(freezeUser);
    boolean updated = Database.getInstance().updateUser(user);

    if (!updated) {
      throw halt(500);
    }

    return "";
  }

}
