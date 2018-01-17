package edu.rosehulman.quota.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.model.Partner;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddPartnerController implements Route {
  @Override
  public Object handle(Request request, Response response) throws Exception {

    JsonObject partnerJsonObject = new JsonParser().parse(request.body()).getAsJsonObject();
    String partnerId = partnerJsonObject.get("partnerId").getAsString();
    String apiKey = partnerJsonObject.get("apiKey").getAsString();

    Partner partner = new Partner();
    partner.setPartnerId(partnerId);
    partner.setApiKey(apiKey);

    Database.getInstance().addPartner(partner);

    return "";
  }
}
