package edu.rosehulman.quota.controller;

import static spark.Spark.halt;

import java.math.BigInteger;
import java.util.Optional;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.Logging;
import edu.rosehulman.quota.client.BillingClient;
import edu.rosehulman.quota.model.UserTier;
import spark.Request;
import spark.Response;
import spark.Route;

public class BillPaidController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");
    String quotaId = request.params(":quotaId");

    String partnerId = Database.getInstance().getPartnerByApi(apiKey).get().getPartnerId();

    Optional<UserTier> userTierOptional = Database.getInstance().getUserTier(partnerId, productId, userId, quotaId);
    if (!userTierOptional.isPresent()) {
      throw halt(404);
    }
    UserTier userTier = userTierOptional.get();
    BigInteger resetValue = BigInteger.ZERO;

    // Save the new value to the database
    userTier.setValue(resetValue.toString());
    boolean updated = Database.getInstance().updateUserTier(userTier);

    if (!updated) {
      throw halt(500);
    }

    // forward to billing
    if (!BillingClient.getInstance().billPaid(partnerId, productId, userId, quotaId)) {
      Logging.errorLog("There was an error forwarding bill paid to the billing server");
      throw halt(500);
    }
    return "";
  }
}
