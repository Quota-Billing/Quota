package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.model.Tier;
import edu.rosehulman.quota.model.UserTier;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;
import java.util.Optional;

import static spark.Spark.halt;

public class IncrementQuotaController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");
    String quotaId = request.params(":quotaId");

    String partnerId = Database.getInstance().getPartnerByApi(apiKey).get().getPartnerId();

    List<Tier> tiers = Database.getInstance().getQuotaTiers(partnerId, productId, quotaId);
    if (tiers.isEmpty()) {
      throw halt(404);
    }
    Tier firstTier = tiers.get(0); // TODO: For now we just get the first tier

    Optional<UserTier> userTierOptional = Database.getInstance().getUserTier(partnerId, productId, userId, quotaId, firstTier.getTierId());
    if (!userTierOptional.isPresent()) {
      throw halt(404);
    }
    UserTier userTier = userTierOptional.get();

    userTier.increment(firstTier, request);

    return "";
  }
}
