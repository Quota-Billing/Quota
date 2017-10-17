package edu.rosehulman.quota.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "quota")
public class Quota {

  @DatabaseField(columnName = "partner_id")
  private String partnerId;

  @DatabaseField(columnName = "product_id")
  private String productId;

  @DatabaseField(columnName = "quota_id")
  private String quotaId;

  @DatabaseField(columnName = "quota_name")
  private String quotaName;

  @DatabaseField(columnName = "type")
  private String type;

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

  public String getQuotaId() {
    return quotaId;
  }

  public void setQuotaId(String quotaId) {
    this.quotaId = quotaId;
  }

  public String getQuotaName() {
    return quotaName;
  }

  public void setQuotaName(String quotaName) {
    this.quotaName = quotaName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
