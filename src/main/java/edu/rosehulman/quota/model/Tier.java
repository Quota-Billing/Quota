package edu.rosehulman.quota.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tier")
public class Tier {

  @DatabaseField(columnName = "partner_id")
  private String partnerId;

  @DatabaseField(columnName = "product_id")
  private String productId;

  @DatabaseField(columnName = "quota_id")
  private String quotaId;

  @DatabaseField(columnName = "tier_id")
  private String tierId;

  @DatabaseField(columnName = "tier_name")
  private String tierName;

  @DatabaseField(columnName = "max")
  private String max;

  @DatabaseField(columnName = "price")
  private String price;

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

  public String getTierId() {
    return tierId;
  }

  public void setTierId(String tierId) {
    this.tierId = tierId;
  }

  public String getTierName() {
    return tierName;
  }

  public void setTierName(String tierName) {
    this.tierName = tierName;
  }

  public String getMax() {
    return max;
  }

  public void setMax(String max) {
    this.max = max;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }
}
