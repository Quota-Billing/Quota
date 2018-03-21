package edu.rosehulman.quota.client;

public class BillingClient {
  private static BillingClient instance;

  public static synchronized BillingClient getInstance() {
    if (instance == null) {
      instance = new BillingClient();
    }
    return instance;
  }

  public String quotaReached(String partnerId, String productId, String userId, String quotaId, String tierId) {
//    try {
//      // TODO: Do something besides return null when not found
//      HttpResponse<String> response = Unirest.post(SystemConfig.getInstance().getBillingServerPath() + "/partner/{partnerId}/product/{productId}/user/{userId}/quotaReached/{quotaId}/tier/{tierId}").routeParam("partnerId", partnerId).routeParam("productId", productId).routeParam("userId", userId).routeParam("quotaId", quotaId).routeParam("tierId", tierId).asString();
//      if (response.getStatus() == 200) {
//        return response.getBody();
//      } else {
//        return null;
//      }
//    } catch (Exception e) {
//      return null;
//    }
    return "";
  }

  public boolean setUserTier(String partnerId, String productId, String quotaId, String tierId, String userId) {
//    try {
//      HttpResponse<String> response = Unirest.post(SystemConfig.getInstance().getBillingServerPath() + "/partner/{partnerId}/product/{productId}/quota/{quotaId}/tier/{tierId}/user/{userId}").routeParam("partnerId", partnerId).routeParam("productId", productId).routeParam("userId", userId).routeParam("quotaId", quotaId).routeParam("tierId", tierId).asString();
//      return response.getStatus() == 200;
//    } catch (Exception e) {
//      return false;
//    }
    return true;
  }
}
