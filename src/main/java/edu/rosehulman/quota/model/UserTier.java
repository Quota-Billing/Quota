package edu.rosehulman.quota.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user_tier")
public class UserTier {

  @DatabaseField(columnName = "user_id")
  private String userId;

  @DatabaseField(columnName = "tier_id")
  private String tierId;

  @DatabaseField(columnName = "quota_id")
  private String quotaId;

  @DatabaseField(columnName = "product_id")
  private String productId;

  @DatabaseField(columnName = "partner_id")
  private String partnerId;

  @DatabaseField(columnName = "value")
  private String value;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getTierId() {
    return tierId;
  }

  public void setTierId(String tierId) {
    this.tierId = tierId;
  }

  public String getQuotaId() {
    return quotaId;
  }

  public void setQuotaId(String quotaId) {
    this.quotaId = quotaId;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
