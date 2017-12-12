package edu.rosehulman.quota.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import edu.rosehulman.quota.SystemConfig;

public class BillingClient {
  private static BillingClient instance;

  public static synchronized BillingClient getInstance() {
    if (instance == null) {
      instance = new BillingClient();
    }
    return instance;
  }

  public String quotaReached(String partnerId, String productId, String userId, String quotaId, String tierId) {
    try {
      // TODO: Do something besides return null when not found
      HttpResponse<String> response = Unirest.post(SystemConfig.getInstance().getBillingServerPath() + "/partner/{partnerId}/product/{productId}/user/{userId}/quotaReached/{quotaId}/tier/{tierId}/").routeParam("partnerId", partnerId).routeParam("productId", productId).routeParam("userId", userId).routeParam("quotaId", quotaId).routeParam("tierId", tierId).asString();
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
