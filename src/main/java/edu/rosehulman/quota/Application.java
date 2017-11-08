package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.*;
import edu.rosehulman.quota.model.*;

import static spark.Spark.*;

public class Application {
  public static void main(String[] args) throws Exception {
    port(8080);

    get("/health", (request, response) -> "OK");

    // Consume an AddUser call from SDK/partner
    post("partner/:partnerId/product/:productId/user/:userId", new AddUserController());

    // Consume a GetUser call from SDK/partner
    get("partner/:partnerId/product/:productId/user/:userId", new GetUserController());

    // Consume a SetConfig call
    post("setConfig", new SetConfigController());

    // Consume DeleteUser call
    delete("partner/:partnerId/product/:productId/user/:userId", new DeleteUserController());

    // Billing calls this endpoint
    get("/partner/:partnerId/product/:productId/user/:userId/quota/:quotaId", new GetQuotaController());

    post("/partner/:partnerId/product/:productId/user/:userId/quota/:quotaId", new IncrementQuotaController());

    try {
      Partner partner = new Partner();
      partner.setApiKey("apiKey");
      partner.setPartnerId("partnerId");
      Database.getInstance().addPartner(partner);

      Product product = new Product();
      product.setPartnerId("partnerId");
      product.setProductId("productId");
      product.setProductName("productName");
      Database.getInstance().addProduct(product);

      User user = new User();
      user.setPartnerId("partnerId");
      user.setProductId("productId");
      user.setUserId("userId");
      Database.getInstance().addUser(user);

      Quota quota = new Quota();
      quota.setPartnerId("partnerId");
      quota.setProductId("productId");
      quota.setQuotaId("quotaId");
      quota.setQuotaName("quotaName");
      quota.setType("type");
      Database.getInstance().addQuota(quota);

      Tier tier = new Tier();
      tier.setPartnerId("partnerId");
      tier.setProductId("productId");
      tier.setQuotaId("quotaId");
      tier.setTierId("tierId");
      tier.setTierName("tierName");
      tier.setMax("5");
      tier.setPrice("23.00");
      Database.getInstance().addTier(tier);

      UserTier userTier = new UserTier();
      userTier.setPartnerId("partnerId");
      userTier.setProductId("productId");
      userTier.setUserId("userId");
      userTier.setQuotaId("quotaId");
      userTier.setTierId("tierId");
      userTier.setValue("0");
      Database.getInstance().addUserTier(userTier);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
