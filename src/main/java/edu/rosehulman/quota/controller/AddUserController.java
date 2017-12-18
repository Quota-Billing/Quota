package edu.rosehulman.quota.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.Logging;
import edu.rosehulman.quota.client.SharedServiceClient;
import edu.rosehulman.quota.model.User;
import org.apache.http.HttpException;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddUserController implements Route {

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String apiKey = request.params(":apiKey");
    String productId = request.params(":productId");

    String partnerId = Database.getInstance().getPartnerByApi(apiKey).get().getPartnerId();

    JsonObject userJsonObject = new JsonParser().parse(request.body()).getAsJsonObject();
    String userId = userJsonObject.get("id").getAsString();

    User user = new User();
    user.setPartnerId(partnerId);
    user.setProductId(productId);
    user.setUserId(userId);

    Database.getInstance().addUser(user);

    // Send the user to Shared
    boolean sharedRes = SharedServiceClient.getInstance().addUser(partnerId, productId, userId);
    if (!sharedRes) {
      HttpException e = new HttpException("Adding user to shared server failed");
      Logging.errorLog(e);
      //throw e;
    }
    /*
     * } catch (Exception e) { e.printStackTrace(); throw e; }
     */
    return "";
  }
}
