package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.AddUserController;
import edu.rosehulman.quota.controller.DeleteUserController;
import edu.rosehulman.quota.controller.GetQuotaController;
import edu.rosehulman.quota.controller.GetUserController;
import edu.rosehulman.quota.controller.SetConfigController;

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
  }
}
 