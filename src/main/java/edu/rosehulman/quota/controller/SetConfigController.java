package edu.rosehulman.quota.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.exceptions.DatabaseException;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.Product;
import edu.rosehulman.quota.model.Quota;
import edu.rosehulman.quota.model.Tier;
import spark.Request;
import spark.Response;
import spark.Route;

public class SetConfigController implements Route {

  public Object handle(Request request, Response response) throws Exception {
    String body = request.body();

    // TODO: All of the database calls assume this is the first time the config has been uploaded

    JsonObject partnerJsonObject = new JsonParser().parse(body).getAsJsonObject();
    String partnerId = partnerJsonObject.get("partnerId").getAsString();
    String apiKey = partnerJsonObject.get("apiKey").getAsString();

    Partner partner = new Partner();
    partner.setPartnerId(partnerId);
    partner.setApiKey(apiKey);

    addPartnerToDatabase(partner);

    JsonArray productsJsonArray = partnerJsonObject.getAsJsonArray("products");
    productsJsonArray.iterator().forEachRemaining(productJsonElement -> {
      JsonObject productJsonObject = productJsonElement.getAsJsonObject();
      String productId = productJsonObject.get("id").getAsString();
      String productName = productJsonObject.get("name").getAsString();

      Product product = new Product();
      product.setPartnerId(partnerId);
      product.setProductId(productId);
      product.setProductName(productName);

      addProductToDatabase(product);

      JsonArray quotasJsonArray = productJsonObject.getAsJsonArray("quotas");
      quotasJsonArray.iterator().forEachRemaining(quotaJsonElement -> {
        JsonObject quotaJsonObject = quotaJsonElement.getAsJsonObject();
        String quotaId = quotaJsonObject.get("id").getAsString();
        String quotaName = quotaJsonObject.get("name").getAsString();
        String type = quotaJsonObject.get("type").getAsString();

        Quota quota = new Quota();
        quota.setPartnerId(partnerId);
        quota.setProductId(productId);
        quota.setQuotaId(quotaId);
        quota.setQuotaName(quotaName);
        quota.setType(type);

        addQuotaToDatabase(quota);

        JsonArray tiersJsonArray = quotaJsonObject.getAsJsonArray("tiers");
        tiersJsonArray.iterator().forEachRemaining(tierJsonElement -> {
          JsonObject tierJsonObject = tierJsonElement.getAsJsonObject();
          String tierId = tierJsonObject.get("id").getAsString();
          String tierName = tierJsonObject.get("name").getAsString();
          String max = tierJsonObject.get("max").getAsString();
          String price = tierJsonObject.get("price").getAsString();
          String graceExtra = tierJsonObject.get("graceExtra").getAsString();

          Tier tier = new Tier();
          tier.setPartnerId(partnerId);
          tier.setProductId(productId);
          tier.setQuotaId(quotaId);
          tier.setTierId(tierId);
          tier.setTierName(tierName);
          tier.setMax(max);
          tier.setPrice(price);
          if (graceExtra != null)
            tier.setGraceExtra(graceExtra);

          addTierToDatabase(tier);
        });
      });
    });

    return "";
  }

  private void addPartnerToDatabase(Partner partner) throws RuntimeException {
    try {
      Database.getInstance().addPartner(partner);
    } catch (Exception e) {
      throw new DatabaseException(e);
    }
  }

  private void addProductToDatabase(Product product) throws RuntimeException {
    try {
      Database.getInstance().addProduct(product);
    } catch (Exception e) {
      throw new DatabaseException(e);
    }
  }

  private void addQuotaToDatabase(Quota quota) throws RuntimeException {
    try {
      Database.getInstance().addQuota(quota);
    } catch (Exception e) {
      throw new DatabaseException(e);
    }
  }

  private void addTierToDatabase(Tier tier) throws RuntimeException {
    try {
      Database.getInstance().addTier(tier);
    } catch (Exception e) {
      throw new DatabaseException(e);
    }
  }
}
