package edu.rosehulman.quota.controller;

import java.util.Optional;
import com.google.gson.JsonObject;
import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.model.Tier;
import edu.rosehulman.quota.model.UserTier;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.halt;

public class GetQuotaSDKController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");
    String quotaId = request.params(":quotaId");

    String partnerId = Database.getInstance().getPartnerByApi(apiKey).get().getPartnerId();

    if (!Database.getInstance().getQuota(partnerId, productId, quotaId).isPresent()) {
      throw halt(404);
    }

    JsonObject json = new JsonObject();

    Optional<UserTier> userTierOptional = Database.getInstance().getUserTier(partnerId, productId, userId, quotaId);
    if (!userTierOptional.isPresent()) {
      throw halt(404);
    }
    UserTier userTier = userTierOptional.get();

    Optional<Tier> tierOptional = Database.getInstance().getTier(partnerId, productId, quotaId, userTier.getTierId());
    if (!tierOptional.isPresent()) {
      throw halt(404);
    }
    Tier tier = tierOptional.get();

    json.addProperty("max", tier.getMax());
    json.addProperty("value", userTier.getValue());

    return json.toString();
  }
}
