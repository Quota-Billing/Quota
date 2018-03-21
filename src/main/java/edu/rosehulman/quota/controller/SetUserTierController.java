package edu.rosehulman.quota.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.Logging;
import edu.rosehulman.quota.client.BillingClient;
import edu.rosehulman.quota.factories.UserTierFactory;
import edu.rosehulman.quota.model.UserTier;
import spark.Request;
import spark.Response;
import spark.Route;

import java.math.BigInteger;
import java.util.Optional;

import static spark.Spark.halt;

public class SetUserTierController implements Route {
  
  private UserTierFactory factory;
  
  public SetUserTierController(UserTierFactory factory) {
    this.factory = factory;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");
    String quotaId = request.params(":quotaId");
    String tierId = request.params(":tierId");

    String partnerId = Database.getInstance().getPartnerByApi(apiKey).get().getPartnerId();

    boolean keepCurrentValue = false;
    if (!request.body().isEmpty()) {
      JsonObject partnerJsonObject = new JsonParser().parse(request.body()).getAsJsonObject();
      keepCurrentValue = partnerJsonObject.get("rollover").getAsBoolean();
    }

    BigInteger currentValue = BigInteger.ZERO;
    Optional<UserTier> userTier = Database.getInstance().getUserTier(partnerId, productId, userId, quotaId);
    if (userTier.isPresent()) {
      if (keepCurrentValue) {
        currentValue = new BigInteger(userTier.get().getValue());
      }
      Database.getInstance().deleteUserTier(partnerId, productId, userId, quotaId);
    }

    UserTier newUserTier = factory.createUserTier(partnerId, productId, quotaId, tierId, userId, currentValue.toString());
    Database.getInstance().addUserTier(newUserTier);

    if (!BillingClient.getInstance().setUserTier(partnerId, productId, quotaId, tierId, userId)) {
      Logging.errorLog("There was an error setting the UserTier in the billing server");
      throw halt(500);
    }

    return "";
  }
}
