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

public class IncrementQuotaController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String partnerId = request.params(":partnerId");
    String productId = request.params(":productId");
    String userId = request.params(":userId");
    String quotaId = request.params(":quotaId");

    List<Tier> tiers = Database.getInstance().getQuotaTiers(partnerId, productId, quotaId);
    if (tiers.isEmpty()) {
      response.status(404);
      return "";
    }
    Tier firstTier = tiers.get(0); // TODO: For now we just get the first tier

    Optional<UserTier> userTierOptional = Database.getInstance().getUserTier(partnerId, productId, userId, quotaId, firstTier.getTierId());
    if (!userTierOptional.isPresent()) {
      System.out.println("");
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
      if (BillingClient.getInstance().quotaReached(partnerId, productId, userId, quotaId)) {
        response.status(403);
      } else {
        throw new HttpException("Quota reached Billing endpoint failed");
      }
    }

    // See if adding one to the quota would go above the quota
    BigInteger valuePlusOne = value.add(BigInteger.ONE);
    if (valuePlusOne.compareTo(max) > 0) {
      // send to billing
      if (BillingClient.getInstance().quotaReached(partnerId, productId, userId, quotaId)) {
        response.status(403);
      } else {
        throw new HttpException("Quota reached Billing endpoint failed");
      }
      return "";
    }

    // Save the new value to the database
    userTier.setValue(valuePlusOne.toString());
    boolean updated = Database.getInstance().updateUserTier(userTier);

    if (updated) {
      response.status(200);
      return "";
    }

    response.status(500);
    return "";
  }
}
