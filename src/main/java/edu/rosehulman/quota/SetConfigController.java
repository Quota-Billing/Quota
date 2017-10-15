package edu.rosehulman.quota;

import spark.Request;
import spark.Response;
import spark.Route;

public class SetConfigController implements Route {
	public Object handle(Request request, Response response) throws Exception {
		// TODO  check for malformed json/null or empty and send then throw exception
		String body = request.body();

		Database.getInstance().setConfig(body);

		return response;
	}
}
