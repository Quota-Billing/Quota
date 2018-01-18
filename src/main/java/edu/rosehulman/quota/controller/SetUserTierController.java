package edu.rosehulman.quota.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.model.Tier;
import edu.rosehulman.quota.model.UserTier;
import spark.Request;
import spark.Response;
import spark.Route;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class SetUserTierController implements Route {

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
    List<Tier> tiers = Database.getInstance().getQuotaTiers(partnerId, productId, quotaId);
    for (Tier tier : tiers) {
      Optional<UserTier> userTier = Database.getInstance().getUserTier(partnerId, productId, userId, quotaId, tier.getTierId());
      if (userTier.isPresent()) {
        if (keepCurrentValue) {
          currentValue = new BigInteger(userTier.get().getValue());
        }
        Database.getInstance().deleteUserTier(partnerId, productId, userId, quotaId, tier.getTierId());
      }
    }

    UserTier newUserTier = new UserTier();
    newUserTier.setPartnerId(partnerId);
    newUserTier.setProductId(productId);
    newUserTier.setQuotaId(quotaId);
    newUserTier.setTierId(tierId);
    newUserTier.setUserId(userId);
    newUserTier.setValue(currentValue.toString());
    Database.getInstance().addUserTier(newUserTier);

    return "";
  }
}
