package edu.rosehulman.quota;

public class Database {

  private static Database instance;

  private Database() {
  }

  public static synchronized Database getInstance() {
    if (instance == null) {
      instance = new Database();
    }
    return instance;
  }

  public Quota getQuota(String partnerId, String productId, String userId, String quotaId) {
    return null; // TODO: This is just mocked in tests
  }
}
