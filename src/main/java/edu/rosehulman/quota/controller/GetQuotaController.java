package edu.rosehulman.quota.controller;

import java.util.List;
import java.util.Optional;

import com.google.gson.JsonObject;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.model.Tier;
import edu.rosehulman.quota.model.UserTier;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetQuotaController implements Route {
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String partnerId = request.params(":partnerId");
    String productId = request.params(":productId");
    String userId = request.params(":userId");
    String quotaId = request.params(":quotaId");

    if (!Database.getInstance().getQuota(partnerId, productId, quotaId).isPresent()) {
      response.status(404);
    }

    List<Tier> tiers = Database.getInstance().getQuotaTiers(partnerId, productId, quotaId);

    JsonObject json = new JsonObject();
    for (Tier tier : tiers) {
      Optional<UserTier> temp = Database.getInstance().getUserTier(partnerId, productId, userId, quotaId, tier.getTierId());
      if (temp.isPresent()) {
        UserTier userTier = temp.get();
        json.addProperty("max", tier.getMax());
        json.addProperty("value", userTier.getValue());
      }
    }

    return json.toString();
  }
}
