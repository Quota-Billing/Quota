package edu.rosehulman.quota.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class BillingClient {
  private static BillingClient instance;

  public static synchronized BillingClient getInstance() {
    if (instance == null) {
      instance = new BillingClient();
    }
    return instance;
  }

  public String quotaReached(String partnerId, String productId, String userId, String quotaId) {
    try {
      // TODO: Put Billing Server path in config and in here
      // TODO: Do something besides return null when not found
      HttpResponse<String> response = Unirest.post("http://srproj-18.csse.rose-hulman.edu:8085/" + "partner/{partnerId}/product/{productId}/user/{userId}/quotaReached/{quotaId}").routeParam("partnerId", partnerId).routeParam("productId", productId).routeParam("userId", userId).routeParam("quotaId", quotaId).asString();
      if (response.getStatus() == 200) {
        return response.getBody();
      } else {
        return null;
      }
    } catch (Exception e) {
      return null;
    }

  }

}
