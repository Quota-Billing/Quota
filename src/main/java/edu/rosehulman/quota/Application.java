package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.AddUserController;
import edu.rosehulman.quota.controller.GetQuotaController;
import edu.rosehulman.quota.controller.SetConfigController;

import static spark.Spark.*;

public class Application {
  public static void main(String[] args) throws Exception {
    port(8080);

    get("/health", (request, response) -> "OK");

    // Consume an AddUser call from SDK/partner
    post("partner/:partnerId/product/:productId/user/:userId", new AddUserController());

    // Consume an SetConfig call
    post("setConfig", new SetConfigController());

    // Billing calls this endpoint
    get("/partner/:partnerId/product/:productId/user/:userId/quota/:quotaId", new GetQuotaController());
  }
}
