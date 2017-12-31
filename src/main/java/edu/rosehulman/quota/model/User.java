package edu.rosehulman.quota.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user")
public class User {

  @DatabaseField(columnName = "partner_id")
  private String partnerId;

  @DatabaseField(columnName = "product_id")
  private String productId;

  @DatabaseField(columnName = "user_id")
  private String userId;

  @DatabaseField(columnName = "frozen")
  private boolean frozen;
  
  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }

  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
