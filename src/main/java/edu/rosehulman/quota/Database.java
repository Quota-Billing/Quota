package edu.rosehulman.quota;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {
  private static Database instance;
  
  private HashMap<String, HashMap<String, ArrayList<String>>> db; // TODO: Joe make it how you want for setConfig
  
  private Database() {
    db = new HashMap<String, HashMap<String, ArrayList<String>>>();
    db.put("Tesla", new HashMap<String, ArrayList<String>>());
    db.get("Tesla").put("ModelS", new ArrayList<String>());
  }
  
  public static synchronized Database getInstance() {
    if (instance == null) {
      instance = new Database();
    }
    return instance;
  }

  public void addUser(String partnerId, String productId, String userId) {
    db.get(partnerId).get(productId).add(userId);
  }

  public Quota getQuota(String partnerId, String productId, String userId, String quotaId) {
    return null; // TODO: This is just mocked in tests
  }
}
