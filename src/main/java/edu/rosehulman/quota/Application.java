package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.*;

import static spark.Spark.*;

public class Application {
  public static void main(String[] args) throws Exception {
    port(8080);

    get("/health", (request, response) -> "OK");

    // Consume an AddUser call from SDK/partner
    post("partnerApi/:apiKey/product/:productId/user", new AddUserController());

    // Consume a (Un)FreezeUser call from Billing
    post("partnerApi/:apiKey/product/:productId/user/:userId", new FreezeUserController());

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
    // get("/partner/:partnerId/product/:productId/user/:userId/quota/:quotaId", new
    // GetQuotaController());

    // increment quota
    post("/partnerApi/:apiKey/product/:productId/user/:userId/quota/:quotaId", new IncrementQuotaController());

    // Consume a GetPartner call from SDK/partner
    get("partnerApi/:apiKey", new GetPartnerByApiController());

    // reset quota call from billing
    put("/partnerApi/:apiKey/product/:productId/user/:userId/quota/:quotaId", new SetQuotaController());

    // Consume a setUserTier call from SDK/partner
    put("/partnerApi/:apiKey/product/:productId/user/:userId/quota/:quotaId/tier/:tierId", new SetUserTierController());

    post("/partner", new AddPartnerController());

    // try {
    // Partner partner = new Partner();
    // partner.setApiKey("apiKey");
    // partner.setPartnerId("partnerId");
    // Database.getInstance().addPartner(partner);
    //
    // Product product = new Product();
    // product.setPartnerId("partnerId");
    // product.setProductId("productId");
    // product.setProductName("productName");
    // Database.getInstance().addProduct(product);
    //
    // Quota quota = new Quota();
    // quota.setQuotaId("quotaId");
    // quota.setQuotaName("quotaName");
    // quota.setPartnerId("partnerId");
    // quota.setProductId("productId");
    // quota.setType("numerical");
    // Database.getInstance().addQuota(quota);
    //
    // Tier tier = new Tier();
    // tier.setPartnerId("partnerId");
    // tier.setProductId("productId");
    // tier.setQuotaId("quotaId");
    // tier.setTierId("tierId");
    // tier.setTierName("tierName");
    // tier.setMax("5");
    // tier.setGraceExtra("1");
    // Database.getInstance().addTier(tier);
    //
    // Tier tier2 = new Tier();
    // tier2.setPartnerId("partnerId");
    // tier2.setProductId("productId");
    // tier2.setQuotaId("quotaId");
    // tier2.setTierId("tierId2");
    // tier2.setTierName("tierName2");
    // tier2.setMax("10");
    // tier2.setGraceExtra("2");
    // Database.getInstance().addTier(tier2);
    //
    // User user = new User();
    // user.setPartnerId("partnerId");
    // user.setProductId("productId");
    // user.setUserId("userId");
    // Database.getInstance().addUser(user);
    //
    // // UserTier automatically populated
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
  }
}
