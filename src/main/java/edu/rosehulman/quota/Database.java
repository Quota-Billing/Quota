package edu.rosehulman.quota;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import edu.rosehulman.quota.model.*;

import java.util.List;
import java.util.Optional;

public class Database {

  private static Database instance;
  private ConnectionSource connectionSource;

  private Database() throws Exception {
    String databaseUrl = SystemConfig.getInstance().getDatabaseUrl();
    connectionSource = new JdbcConnectionSource(databaseUrl);
  }

  public static synchronized Database getInstance() throws Exception {
    if (instance == null) {
      instance = new Database();
    }
    return instance;
  }

  public Optional<Partner> getPartner(String partnerId) throws Exception {
    List<Partner> partners = getPartnerDao().query(getPartnerDao().queryBuilder().where().eq("partner_id", partnerId).prepare());
    return partners.isEmpty() ? Optional.empty() : Optional.ofNullable(partners.get(0));
  }

  public Optional<Partner> getPartnerByApi(String apiKey) throws Exception {
    List<Partner> partners = getPartnerDao().query(getPartnerDao().queryBuilder().where().eq("api_key", apiKey).prepare());
    return partners.isEmpty() ? Optional.empty() : Optional.ofNullable(partners.get(0));
  }

  public void addPartner(Partner partner) throws Exception {
    getPartnerDao().create(partner);
  }

  public Optional<Product> getProduct(String partnerId, String productId) throws Exception {
    List<Product> products = getProductDao().query(getProductDao().queryBuilder().where().eq("partner_id", partnerId).and().eq("product_id", productId).prepare());
    return products.isEmpty() ? Optional.empty() : Optional.ofNullable(products.get(0));
  }

  public void addProduct(Product product) throws Exception {
    getProductDao().create(product);
  }

  public Optional<Quota> getQuota(String partnerId, String productId, String quotaId) throws Exception {
    List<Quota> quotas = getQuotaDao().query(getQuotaDao().queryBuilder().where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("quota_id", quotaId).prepare());
    return quotas.isEmpty() ? Optional.empty() : Optional.ofNullable(quotas.get(0));
  }

  public void addQuota(Quota quota) throws Exception {
    getQuotaDao().create(quota);
  }

  public List<Tier> getQuotaTiers(String partnerId, String productId, String quotaId) throws Exception {
    return getTierDao().query(getTierDao().queryBuilder().where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("quota_id", quotaId).prepare());
  }

  public Optional<Tier> getTier(String partnerId, String productId, String quotaId, String tierId) throws Exception {
    List<Tier> tiers = getTierDao().query(getTierDao().queryBuilder().where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("quota_id", quotaId).and().eq("tier_id", tierId).prepare());
    return tiers.isEmpty() ? Optional.empty() : Optional.ofNullable(tiers.get(0));
  }

  public void addTier(Tier tier) throws Exception {
    getTierDao().create(tier);
  }

  public boolean deleteTier(String partnerId, String productId, String quotaId, String tierId, String newTierId) throws Exception {
    String newTier = newTierId;
    List<Tier> tiers = getQuotaTiers(partnerId, productId, quotaId);
    if (newTierId.isEmpty()) {
      // If a new tier isn't specified we use the first in the tier list
      newTier = tiers.get(0).getTierId();
    } else {
      boolean ret = true;
      for (Tier tier: tiers) {
        if (tier.getTierId().equals(newTierId)) {
          ret = false;
        }
      }
      if (ret) {
        // The new tier doesn't exist in db
        return false;
      }
    }
    changeUsersToNewTier(partnerId, productId, quotaId, tierId, newTier);
    DeleteBuilder<Tier, String> builder = getTierDao().deleteBuilder();
    builder.where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("quota_id", quotaId).and().eq("tier_id", tierId);
    return builder.delete() > 0;
  }
  
  public void changeUsersToNewTier(String partnerId, String productId, String quotaId, String prevTierId, String newTierId) throws Exception {
    List<UserTier> userTiers = getUserTierDao().query(getUserTierDao().queryBuilder().where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("quota_id", quotaId).and().eq("tier_id", prevTierId).prepare());
    for (UserTier ut: userTiers) {
      changeUserTier(ut, newTierId);
    }
  }

  public boolean updateUser(User user) throws Exception {
    UpdateBuilder<User, String> updateBuilder = getUserDao().updateBuilder();
    updateBuilder.updateColumnValue("frozen", user.isFrozen());
    updateBuilder.where().eq("partner_id", user.getPartnerId()).and().eq("product_id", user.getProductId()).and().eq("user_id", user.getUserId());
    return updateBuilder.update() == 1;
  }

  public Optional<User> getUser(String partnerId, String productId, String userId) throws Exception {
    List<User> users = getUserDao().query(getUserDao().queryBuilder().where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("user_id", userId).prepare());
    return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.get(0));
  }

  public void addUser(User user) throws Exception {
    getUserDao().create(user);
  }

  public boolean deleteUser(String partnerId, String productId, String userId) throws Exception {
    DeleteBuilder<User, String> builder = getUserDao().deleteBuilder();
    builder.where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("user_id", userId);
    return builder.delete() > 0;
  }

  public void addUserTier(UserTier userTier) throws Exception {
    getUserTierDao().create(userTier);
  }

  public Optional<UserTier> getUserTier(String partnerId, String productId, String userId, String quotaId) throws Exception {
    List<UserTier> tiers = getUserTierDao().query(getUserTierDao().queryBuilder().where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("user_id", userId).and().eq("quota_id", quotaId).prepare());
    return tiers.isEmpty() ? Optional.empty() : Optional.ofNullable(tiers.get(0));
  }

  public boolean changeUserTier(UserTier userTier, String newTierId) throws Exception {
    UpdateBuilder<UserTier, String> updateBuilder = getUserTierDao().updateBuilder();
    updateBuilder.updateColumnValue("tier_id", newTierId);
    updateBuilder.where().eq("partner_id", userTier.getPartnerId()).and().eq("product_id", userTier.getProductId()).and().eq("user_id", userTier.getUserId()).and().eq("quota_id", userTier.getQuotaId());
    return updateBuilder.update() == 1;
  }

  public boolean updateUserTier(UserTier userTier) throws Exception {
    UpdateBuilder<UserTier, String> updateBuilder = getUserTierDao().updateBuilder();
    updateBuilder.updateColumnValue("value", userTier.getValue());
    updateBuilder.where().eq("partner_id", userTier.getPartnerId()).and().eq("product_id", userTier.getProductId()).and().eq("user_id", userTier.getUserId()).and().eq("quota_id", userTier.getQuotaId());
    return updateBuilder.update() == 1;
  }

  public boolean deleteUserTier(String partnerId, String productId, String userId, String quotaId) throws Exception {
    DeleteBuilder<UserTier, String> builder = getUserTierDao().deleteBuilder();
    builder.where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("user_id", userId).and().eq("quota_id", quotaId);
    return builder.delete() > 0;
  }

  private Dao<Partner, String> getPartnerDao() throws Exception {
    return DaoManager.createDao(connectionSource, Partner.class);
  }

  private Dao<Product, String> getProductDao() throws Exception {
    return DaoManager.createDao(connectionSource, Product.class);
  }

  private Dao<Quota, String> getQuotaDao() throws Exception {
    return DaoManager.createDao(connectionSource, Quota.class);
  }

  private Dao<Tier, String> getTierDao() throws Exception {
    return DaoManager.createDao(connectionSource, Tier.class);
  }

  private Dao<User, String> getUserDao() throws Exception {
    return DaoManager.createDao(connectionSource, User.class);
  }

  private Dao<UserTier, String> getUserTierDao() throws Exception {
    return DaoManager.createDao(connectionSource, UserTier.class);
  }
}
