package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.model.Tier;
import edu.rosehulman.quota.model.UserTier;
import spark.Request;
import spark.Response;
import spark.Route;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SetQuotaController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");
    String quotaId = request.params(":quotaId");

    String partnerId = Database.getInstance().getPartnerByApi(apiKey).get().getPartnerId();

    List<Tier> tiers = Database.getInstance().getQuotaTiers(partnerId, productId, quotaId);
    if (tiers.isEmpty()) {
      response.status(404);
      return "";
    }
    Tier firstTier = tiers.get(0); // TODO: For now we just get the first tier

    Optional<UserTier> userTierOptional = Database.getInstance().getUserTier(partnerId, productId, userId, quotaId, firstTier.getTierId());
    if (!userTierOptional.isPresent()) {
      response.status(404);
      return "";
    }
    UserTier userTier = userTierOptional.get();
    BigInteger resetValue = BigInteger.ZERO;
    if (!request.body().isEmpty()) {
      JsonObject partnerJsonObject = new JsonParser().parse(request.body()).getAsJsonObject();
      resetValue = (new BigInteger(partnerJsonObject.get("count").getAsString()));
    }

    // Save the new value to the database
    userTier.setValue(resetValue.toString());
    boolean updated = Database.getInstance().updateUserTier(userTier);

    if (updated) {
      response.status(200);
      return "";
    }

    response.status(500);
    return "";
  }
}
