package edu.rosehulman.quota;

import static spark.Spark.get;
import static spark.Spark.port;

public class Application {
  public static void main(String[] args) {
    port(8080);

    get("/health", (request, response) -> "OK");
  }
}
