package edu.rosehulman.quota.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "partner")
public class Partner {

  @DatabaseField(columnName = "partner_id")
  private String partnerId;

  @DatabaseField(columnName = "api_key")
  private String apiKey;

  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }
}
