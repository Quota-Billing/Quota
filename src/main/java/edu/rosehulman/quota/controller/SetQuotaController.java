package edu.rosehulman.quota.controller;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.UserTier;
import spark.Request;
import spark.Response;
import spark.Route;

import java.math.BigInteger;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static spark.Spark.halt;

public class SetQuotaController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");
    String userId = request.params(":userId");
    String quotaId = request.params(":quotaId");

    Optional<Partner> optPartner = Database.getInstance().getPartnerByApi(apiKey);
    if (!optPartner.isPresent()) {
      throw halt(404, "Partner not present");
    }
    String partnerId = optPartner.get().getPartnerId();
    
    Optional<UserTier> userTierOptional = Database.getInstance().getUserTier(partnerId, productId, userId, quotaId);
    if (!userTierOptional.isPresent()) {
      throw halt(404, "User not present");
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

    if (!updated) {
      throw halt(500, "Database not updated");
    }

    return "";
  }
}
