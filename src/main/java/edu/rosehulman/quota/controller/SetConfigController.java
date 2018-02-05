package edu.rosehulman.quota.controller;

import static spark.Spark.halt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.Parser;
import edu.rosehulman.quota.StorageParser;
import edu.rosehulman.quota.TimeParser;
import edu.rosehulman.quota.exceptions.DatabaseException;
import edu.rosehulman.quota.model.Product;
import edu.rosehulman.quota.model.Quota;
import edu.rosehulman.quota.model.Tier;
import spark.Request;
import spark.Response;
import spark.Route;

public class SetConfigController implements Route {

  private Map<String, Parser> map;

  public SetConfigController() {
    map = new HashMap<String, Parser>();
    map.put("storage", new StorageParser());
    map.put("time", new TimeParser());
  }

  public Object handle(Request request, Response response) throws Exception {
    String body = request.body();

    // TODO: All of the database calls assume this is the first time the config has
    // been uploaded

    JsonObject partnerJsonObject = new JsonParser().parse(body).getAsJsonObject();

    String partnerId = partnerJsonObject.get("partnerId").getAsString();

    List<Product> dbProducts = Database.getInstance().getProducts(partnerId);

    JsonArray productsJsonArray = partnerJsonObject.getAsJsonArray("products");
    productsJsonArray.iterator().forEachRemaining(productJsonElement -> {
      JsonObject productJsonObject = productJsonElement.getAsJsonObject();
      String productId = productJsonObject.get("productId").getAsString();
      String productName = productJsonObject.get("name").getAsString();

      Product product = new Product();
      product.setPartnerId(partnerId);
      product.setProductId(productId);
      product.setProductName(productName);

      // loop through products and see if in database
      boolean found = false;
      for (int i = 0; i < dbProducts.size(); i++) {
        if (dbProducts.get(i).getProductId().equals(productId)) {
          // check if need to update
          if (!productName.equals(dbProducts.get(i).getProductName())) {
            updateProductNameInDataBase(product);
          }
          found = true;
          dbProducts.remove(i);
          break;
        }
      }
      // not in Database, need to add
      if (!found) {
        addProductToDatabase(product);
      }
      
      try {
        List<Quota> dbQuotas = Database.getInstance().getQuotas(partnerId, productId);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      JsonArray quotasJsonArray = productJsonObject.getAsJsonArray("quotas");
      quotasJsonArray.iterator().forEachRemaining(quotaJsonElement -> {
        JsonObject quotaJsonObject = quotaJsonElement.getAsJsonObject();
        String quotaId = quotaJsonObject.get("quotaId").getAsString();
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
          String tierId = tierJsonObject.get("tierId").getAsString();
          String tierName = tierJsonObject.get("name").getAsString();
          String max = tierJsonObject.get("max").getAsString();
          String graceExtra = tierJsonObject.get("graceExtra").getAsString();

          if (map.containsKey(type)) {
            max = map.get(type).parse(max);
            graceExtra = map.get(type).parse(graceExtra);
          }

          Tier tier = new Tier();
          tier.setPartnerId(partnerId);
          tier.setProductId(productId);
          tier.setQuotaId(quotaId);
          tier.setTierId(tierId);
          tier.setTierName(tierName);
          tier.setMax(max);
          if (graceExtra != null)
            tier.setGraceExtra(graceExtra);

          addTierToDatabase(tier);
        });
      });

      // check if any remaining products; if so, they were removed from config, so
      // remove from database
      if (!dbProducts.isEmpty()) {
        for (Product p : dbProducts) {
          deleteProduct(p);
        }
      }
    });

    return "";
  }

  private void addProductToDatabase(Product product) throws RuntimeException {
    try {
      Database.getInstance().addProduct(product);
    } catch (Exception e) {
      throw new DatabaseException(e);
    }
  }

  private void updateProductNameInDataBase(Product product) throws RuntimeException {
    try {
      boolean updated = Database.getInstance().updateProductName(product);
      if (!updated) {
        throw halt(500);
      }
    } catch (Exception e) {
      throw new DatabaseException(e);
    }
  }

  private void deleteProduct(Product product) {
    try {
      boolean deleted = Database.getInstance().deleteProduct(product);
      if (!deleted) {
        throw halt(500);
      }
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
