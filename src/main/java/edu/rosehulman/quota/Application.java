package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.AddUserController;
import edu.rosehulman.quota.controller.DeleteUserController;
import edu.rosehulman.quota.controller.GetQuotaController;
import edu.rosehulman.quota.controller.GetUserController;
import edu.rosehulman.quota.controller.SetConfigController;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.Product;
import edu.rosehulman.quota.model.Quota;

import static spark.Spark.*;

public class Application {
  public static void main(String[] args) throws Exception {
    port(8080);

    get("/health", (request, response) -> "OK");

    // Consume an AddUser call from SDK/partner
    post("partner/:partnerId/product/:productId/user/:userId", new AddUserController());

    // Consume a GetUser call from SDK/partner
    get("partner/:partnerId/product/:productId/user/:userId", new GetUserController());

    // Consume a GetQuota call from SDK/partner
    get("partner/:partnerId/product/:productId/user/:userId/quota/:quotaId", new GetQuotaController());

    // Consume a SetConfig call
    post("setConfig", new SetConfigController());

    // Consume DeleteUser call
    delete("partner/:partnerId/product/:productId/user/:userId", new DeleteUserController());

    // TODO change this to accept billing requests
    // Billing calls this endpoint
//    get("/partner/:partnerId/product/:productId/user/:userId/quota/:quotaId", new GetQuotaController());

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

      Quota quota = new Quota();
      quota.setQuotaId("quotaId");
      quota.setQuotaName("quotaName");
      quota.setPartnerId("partnerId");
      quota.setProductId("productId");
      quota.setType("numerical");
      Database.getInstance().addQuota(quota);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
