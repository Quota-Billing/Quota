package edu.rosehulman.quota.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "product")
public class Product {

  @DatabaseField(columnName = "partner_id")
  private String partnerId;

  @DatabaseField(columnName = "product_id")
  private String productId;

  @DatabaseField(columnName = "product_name")
  private String productName;

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

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }
}
