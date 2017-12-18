package edu.rosehulman.quota.model;

public class Config {

  private String databaseUrl;
  private String sharedServerPath;
  private String billingServerPath;

  public String getSharedServerPath() {
    return sharedServerPath;
  }

  public String getDatabaseUrl() {
    return databaseUrl;
  }

  public String getBillingServerPath() {
    return billingServerPath;
  }
}
