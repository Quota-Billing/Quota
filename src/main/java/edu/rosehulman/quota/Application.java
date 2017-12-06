package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.*;

import static spark.Spark.*;

public class Application {
  public static void main(String[] args) throws Exception {
    port(8080);

    get("/health", (request, response) -> "OK");

    // Consume an AddUser call from SDK/partner
    post("partnerApi/:apiKey/product/:productId/user", new AddUserController());

    // Consume a GetUser call from SDK/partner
    get("partnerApi/:apiKey/product/:productId/user/:userId", new GetUserController());

    // Consume a GetQuota call from SDK/partner
    get("partnerApi/:apiKey/product/:productId/user/:userId/quota/:quotaId", new GetQuotaController());

    // Consume a SetConfig call
    post("setConfig", new SetConfigController());

    // Consume DeleteUser call
    delete("partnerApi/:apiKey/product/:productId/user/:userId", new DeleteUserController());

    // TODO change this to accept billing requests
    // Billing calls this endpoint
    //    get("/partner/:partnerId/product/:productId/user/:userId/quota/:quotaId", new GetQuotaController());

    // increment quota
    post("/partnerApi/:apiKey/product/:productId/user/:userId/quota/:quotaId", new IncrementQuotaController());

    // Consume a GetPartner call from SDK/partner
    get("partnerApi/:apiKey", new GetPartnerByApiController());

//    try {
//      Partner partner = new Partner();
//      partner.setApiKey("apiKey");
//      partner.setPartnerId("partnerId");
//      Database.getInstance().addPartner(partner);
//
//      Product product = new Product();
//      product.setPartnerId("partnerId");
//      product.setProductId("productId");
//      product.setProductName("productName");
//      Database.getInstance().addProduct(product);
//
//      Quota quota = new Quota();
//      quota.setQuotaId("quotaId");
//      quota.setQuotaName("quotaName");
//      quota.setPartnerId("partnerId");
//      quota.setProductId("productId");
//      quota.setType("numerical");
//      Database.getInstance().addQuota(quota);
//
//      User user = new User();
//      user.setPartnerId("partnerId");
//      user.setProductId("productId");
//      user.setUserId("userId");
//      Database.getInstance().addUser(user);
//
//      Tier tier = new Tier();
//      tier.setPartnerId("partnerId");
//      tier.setProductId("productId");
//      tier.setQuotaId("quotaId");
//      tier.setTierId("tierId");
//      tier.setTierName("tierName");
//      tier.setMax("5");
//      tier.setPrice("23.00");
//      Database.getInstance().addTier(tier);
//
//      // UserTier automatically populated
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
  }
}
