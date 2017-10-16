package edu.rosehulman.quota;

import org.apache.http.HttpException;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddUserController implements Route {

	@Override
	public Object handle(Request request, Response response) throws Exception {
		String partnerId = request.params(":partnerId");
		String productId = request.params(":productId");
		String userId = request.params(":userId");

		// Add the user to our database
		boolean added = Database.getInstance().addUser(partnerId, productId, userId);
		if (!added) {
			throw new HttpException("Adding user to database failed");
		}

		// Send the user to Shared
		boolean sharedRes = SharedServiceClient.getInstance().addUser(partnerId, productId, userId);
		if (!sharedRes) {
			throw new HttpException("Adding user to shared server failed");
		}

		return response;
	}
}
