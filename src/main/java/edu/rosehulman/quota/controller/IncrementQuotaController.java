package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.Logging;
import edu.rosehulman.quota.client.BillingClient;
import edu.rosehulman.quota.model.Tier;
import edu.rosehulman.quota.model.User;
import edu.rosehulman.quota.model.UserTier;
import spark.Request;
import spark.Response;
import spark.Route;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import javax.xml.crypto.Data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static spark.Spark.halt;

public class IncrementQuotaController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");
    String quotaId = request.params(":quotaId");

    String partnerId = Database.getInstance().getPartnerByApi(apiKey).get().getPartnerId();

    Optional<User> userOptional = Database.getInstance().getUser(partnerId, productId, userId);
    if (!userOptional.isPresent()) {
      throw halt(404);
    }
    User user = userOptional.get();
    
    // if user is frozen, cannot increment at all
    if(user.isFrozen()) {
      throw halt(403);
    }
    
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

    // TODO: For now we use BigInteger as the data type for the value and max
    BigInteger value = new BigInteger(userTier.getValue());
    BigInteger max = new BigInteger(firstTier.getMax());
    BigInteger graceExtra = new BigInteger(firstTier.getGraceExtra());
    BigInteger maxPlusGraceExtra = max.add(graceExtra);

    // See if we are at or above the quota already
    if (value.compareTo(maxPlusGraceExtra) >= 0) {
      // TODO should we only send once or send if above also just to be safe?
      // send to billing
      String bill = BillingClient.getInstance().quotaReached(partnerId, productId, userId, quotaId, userTier.getTierId());
      if (bill != null) {
        throw halt(403, bill);
      } else {
        Logging.errorLog("Quota reached Billing endpoint failed");
        throw halt(500);
      }
    }

    // See if adding one to the quota would go above the quota
    BigInteger incrementedValue = value.add(BigInteger.ONE);
    if (!request.body().isEmpty()) {
      JsonObject partnerJsonObject = new JsonParser().parse(request.body()).getAsJsonObject();
      incrementedValue = value.add(new BigInteger(partnerJsonObject.get("count").getAsString()));
    }
    if (incrementedValue.compareTo(maxPlusGraceExtra) > 0) {
      // send to billing
      String bill = BillingClient.getInstance().quotaReached(partnerId, productId, userId, quotaId, userTier.getTierId());
      if (bill != null) {
        throw halt(403, bill);
      } else {
        Logging.errorLog("Quota reached Billing endpoint failed");
        throw halt(500);
      }
    }

    // Save the new value to the database
    userTier.setValue(incrementedValue.toString());
    boolean updated = Database.getInstance().updateUserTier(userTier);

    if (!updated) {
      throw halt(500);
    }

    return "";
  }
}
