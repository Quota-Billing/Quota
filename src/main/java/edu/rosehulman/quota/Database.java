package edu.rosehulman.quota;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import edu.rosehulman.quota.model.*;

import java.util.List;
import java.util.Optional;

public class Database {

  private static Database instance;
  private ConnectionSource connectionSource;

  private Database() throws Exception {
    String databaseUrl = "jdbc:postgresql://localhost:5432/quota"; // TODO: Set in config file
    connectionSource = new JdbcConnectionSource(databaseUrl);
  }

  public static synchronized Database getInstance() throws Exception {
    if (instance == null) {
      instance = new Database();
    }
    return instance;
  }

  public Optional<Partner> getPartner(String partnerId) throws Exception {
    List<Partner> partners = getPartnerDao()
        .query(getPartnerDao().queryBuilder().where().eq("partner_id", partnerId).prepare());
    return partners.isEmpty() ? Optional.empty() : Optional.ofNullable(partners.get(0));
  }

  public void addPartner(Partner partner) throws Exception {
    getPartnerDao().create(partner);
  }

  public Optional<Product> getProduct(String partnerId, String productId) throws Exception {
    List<Product> products = getProductDao().query(
        getProductDao().queryBuilder().where().eq("partner_id", partnerId).and().eq("product_id", productId).prepare());
    return products.isEmpty() ? Optional.empty() : Optional.ofNullable(products.get(0));
  }

  public void addProduct(Product product) throws Exception {
    getProductDao().create(product);
  }

  public Optional<Quota> getQuota(String partnerId, String productId, String quotaId) throws Exception {
    List<Quota> quotas = getQuotaDao().query(getQuotaDao().queryBuilder().where().eq("partner_id", partnerId).and()
        .eq("product_id", productId).and().eq("quota_id", quotaId).prepare());
    return quotas.isEmpty() ? Optional.empty() : Optional.ofNullable(quotas.get(0));
  }

  public void addQuota(Quota quota) throws Exception {
    getQuotaDao().create(quota);
  }

  public Optional<Tier> getTier(String partnerId, String productId, String quotaId, String tierId) throws Exception {
    List<Tier> tiers = getTierDao().query(getTierDao().queryBuilder().where().eq("partner_id", partnerId).and()
        .eq("product_id", productId).and().eq("quota_id", quotaId).and().eq("tier_id", tierId).prepare());
    return tiers.isEmpty() ? Optional.empty() : Optional.ofNullable(tiers.get(0));
  }

  public void addTier(Tier tier) throws Exception {
    getTierDao().create(tier);
  }

  public Optional<User> getUser(String partnerId, String productId, String userId) throws Exception {
    List<User> users = getUserDao().query(getUserDao().queryBuilder().where().eq("partner_id", partnerId).and()
        .eq("product_id", productId).and().eq("user_id", userId).prepare());
    return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.get(0));
  }

  public void addUser(User user) throws Exception {
    getUserDao().create(user);
  }


  // TODO does this work or need to extract parts?
  public void deleteUser(User user) throws Exception {
    getUserDao().delete(user);
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

}
