package edu.rosehulman.quota;

import spark.Request;
import spark.Response;
import spark.Route;

public class SetConfigController implements Route {
	public Object handle(Request request, Response response) throws Exception {
		// change this to 
		String body = request.body();

	    Database.getInstance().setConfig(body);

	    return "";
	}	
}
