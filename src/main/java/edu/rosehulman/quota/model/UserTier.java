package edu.rosehulman.quota.model;

import java.math.BigInteger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import edu.rosehulman.quota.Database;
import edu.rosehulman.quota.Logging;
import edu.rosehulman.quota.client.BillingClient;
import spark.Request;
import static spark.Spark.halt;

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

  public void increment(Tier firstTier, Request request, User user) throws Exception {
    // TODO: For now we use BigInteger as the data type for the value and max
    // if user is frozen for any reason, no need to increment
    if (user.isFrozen()) {
      throw halt(403);
    }

    BigInteger value = new BigInteger(this.value);
    BigInteger max = firstTier.calculateMax();

    // See if we are at or above the quota already
    if (value.compareTo(max) >= 0) {
      // TODO should we only send once or send if above also just to be safe?
      // send to billing
      String bill = BillingClient.getInstance().quotaReached(partnerId, productId, userId, quotaId, this.tierId);
      if (bill != null) {
        throw halt(403, bill);
      } else {
        Logging.errorLog("Quota reached Billing endpoint failed");
        throw halt(500);
      }
    }

    // See if adding one to the quota would go above the quota
    BigInteger incrementedValue = value.add(BigInteger.ONE);
    if (!request.body().isEmpty()) {
      JsonObject partnerJsonObject = new JsonParser().parse(request.body()).getAsJsonObject();
      incrementedValue = value.add(new BigInteger(partnerJsonObject.get("count").getAsString()));
    }
    if (incrementedValue.compareTo(max) > 0) {
      // send to billing
      String bill = BillingClient.getInstance().quotaReached(this.partnerId, this.productId, this.userId, this.quotaId, this.tierId);
      if (bill != null) {
        throw halt(403, bill);
      } else {
        Logging.errorLog("Quota reached Billing endpoint failed");
        throw halt(500);
      }
    }

    // Save the new value to the database
    this.value = incrementedValue.toString();
    boolean updated = Database.getInstance().updateUserTier(this);

    if (!updated) {
      throw halt(500);
    }
  }
}
