package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.client.BillingClient;
import edu.rosehulman.quota.model.Tier;
import edu.rosehulman.quota.model.UserTier;
import spark.Request;
import spark.Response;
import spark.Route;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

    // TODO: For now we use BigInteger as the data type for the value and max
    BigInteger value = new BigInteger(userTier.getValue());
    BigInteger max = new BigInteger(firstTier.getMax());

    // See if we are at or above the quota already
    if (value.compareTo(max) >= 0) {
      // TODO should we only send once or send if above also just to be safe?
      // send to billing
      String bill = BillingClient.getInstance().quotaReached(partnerId, productId, userId, quotaId, userTier.getTierId());
      if (bill != null) {
        response.status(403);
        return bill;
      } else {
        throw new HttpException("Quota reached Billing endpoint failed");
      }
    }

    // See if adding one to the quota would go above the quota
    BigInteger incrementedValue = value.add(BigInteger.ONE);
    if (!request.body().isEmpty()) {
      JsonObject partnerJsonObject = new JsonParser().parse(request.body()).getAsJsonObject();
      incrementedValue = value.add(new BigInteger(partnerJsonObject.get("count").getAsString()));
    }
    if (incrementedValue.compareTo(max) > 0) {
      // send to billing
      String bill = BillingClient.getInstance().quotaReached(partnerId, productId, userId, quotaId, userTier.getTierId());
      if (bill != null) {
        response.status(403);
        return bill;
      } else {
        throw new HttpException("Quota reached Billing endpoint failed");
      }
    }

    // Save the new value to the database
    userTier.setValue(incrementedValue.toString());
    boolean updated = Database.getInstance().updateUserTier(userTier);

    if (updated) {
      response.status(200);
      return "";
    }

    response.status(500);
    return "";
  }
}
