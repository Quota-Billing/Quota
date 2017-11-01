package edu.rosehulman.quota.model;

public class Config {

  private String databaseUrl;
  private String sharedServerPath;

  public String getSharedServerPath() {
    return sharedServerPath;
  }

  public String getDatabaseUrl() {
    return databaseUrl;
  }
}
