package edu.rosehulman.quota;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetQuotaController implements Route {
	@Override
	public Object handle(Request request, Response response) throws Exception {
		String partnerId = request.params(":partnerId");
		String productId = request.params(":productId");
		String userId = request.params(":userId");
		String quotaId = request.params(":quotaId");

		Quota quota = Database.getInstance().getQuota(partnerId, productId, userId, quotaId);

		return new Gson().toJson(quota);
	}
}
