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

  public List<Quota> getAllQuotasForProduct(String partnerId, String productId) throws Exception {
    return getQuotaDao().query(getQuotaDao().queryBuilder().where().eq("partner_id", partnerId).and().eq("product_id", productId).prepare());
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
    populateUserTier(tier);
  }

  private void populateUserTier(Tier tier) throws Exception {
    List<User> users = getAllUsersForProduct(tier.getPartnerId(), tier.getProductId());
    for (User user : users) {
      UserTier userTier = new UserTier();
      userTier.setPartnerId(user.getPartnerId());
      userTier.setProductId(user.getProductId());
      userTier.setQuotaId(tier.getQuotaId());
      userTier.setUserId(user.getUserId());
      userTier.setTierId(tier.getTierId());
      userTier.setValue("0");
      addUserTier(userTier);
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

  public List<User> getAllUsersForProduct(String partnerId, String productId) throws Exception {
    return getUserDao().query(getUserDao().queryBuilder().where().eq("partner_id", partnerId).and().eq("product_id", productId).prepare());
  }

  public void addUser(User user) throws Exception {
    getUserDao().create(user);
    populateUserTiers(user);
  }

  private void populateUserTiers(User user) throws Exception {
    List<Quota> quotas = getAllQuotasForProduct(user.getPartnerId(), user.getProductId());
    for (Quota quota : quotas) {
      List<Tier> tiers = getQuotaTiers(quota.getPartnerId(), quota.getProductId(), quota.getQuotaId());
      for (Tier tier : tiers) { // TODO: Check for 'current' tier in the future
        UserTier userTier = new UserTier();
        userTier.setPartnerId(user.getPartnerId());
        userTier.setProductId(user.getProductId());
        userTier.setQuotaId(quota.getQuotaId());
        userTier.setUserId(user.getUserId());
        userTier.setTierId(tier.getTierId());
        userTier.setValue("0");
        addUserTier(userTier);
      }
    }
  }

  public boolean deleteUser(String partnerId, String productId, String userId) throws Exception {
    DeleteBuilder<User, String> builder = getUserDao().deleteBuilder();
    builder.where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("user_id", userId);
    return builder.delete() > 0;
  }

  public void addUserTier(UserTier userTier) throws Exception {
    getUserTierDao().create(userTier);
  }

  public Optional<UserTier> getUserTier(String partnerId, String productId, String userId, String quotaId, String tierId) throws Exception {
    List<UserTier> tiers = getUserTierDao().query(getUserTierDao().queryBuilder().where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("user_id", userId).and().eq("quota_id", quotaId).and().eq("tier_id", tierId).prepare());
    return tiers.isEmpty() ? Optional.empty() : Optional.ofNullable(tiers.get(0));
  }

  public boolean updateUserTier(UserTier userTier) throws Exception {
    UpdateBuilder<UserTier, String> updateBuilder = getUserTierDao().updateBuilder();
    updateBuilder.updateColumnValue("value", userTier.getValue());
    updateBuilder.where().eq("partner_id", userTier.getPartnerId()).and().eq("product_id", userTier.getProductId()).and().eq("user_id", userTier.getUserId()).and().eq("quota_id", userTier.getQuotaId()).and().eq("tier_id", userTier.getTierId());
    return updateBuilder.update() == 1;
  }

  public boolean deleteUserTier(String partnerId, String productId, String userId, String quotaId, String tierId) throws Exception {
    DeleteBuilder<UserTier, String> builder = getUserTierDao().deleteBuilder();
    builder.where().eq("partner_id", partnerId).and().eq("product_id", productId).and().eq("user_id", userId).and().eq("quota_id", quotaId).and().eq("tier_id", tierId);
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
