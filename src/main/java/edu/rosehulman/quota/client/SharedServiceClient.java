package edu.rosehulman.quota.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class SharedServiceClient {

  private static SharedServiceClient instance;

  public static synchronized SharedServiceClient getInstance() {
    if (instance == null) {
      instance = new SharedServiceClient();
    }
    return instance;
  }

  public boolean addUser(String partnerId, String productId, String userId) {
    try {
      // TODO: Put Shared Server path in config and in here
      HttpResponse<String> response = Unirest
          .post("http://localhost:8084/partner/{partnerId}/product/{productId}/user/{userId}")
          .routeParam("partnerId", partnerId).routeParam("productId", productId).routeParam("userId", userId)
          .asString();
      return response.getStatus() == 200;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean deleteUser(String partnerId, String productId, String userId) {
    try {
      // TODO: Put Shared Server path in config and in here
      HttpResponse<String> response = Unirest
          .delete("http://localhost:8084/partner/{partnerId}/product/{productId}/user/{userId}")
          .routeParam("partnerId", partnerId).routeParam("productId", productId).routeParam("userId", userId)
          .asString();
      return response.getStatus() == 200; // or 202???
    } catch (Exception e) {
      return false;
    }
  }
}
