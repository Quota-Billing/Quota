package edu.rosehulman.quota;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class Application {
  public static void main(String[] args) {
    port(8080);

    get("/health", (request, response) -> "OK");
    post(Paths.ADD_USER, new AddUserController()); // Consume an AddUser call from SDK/partner
  }
}
