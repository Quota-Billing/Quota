package edu.rosehulman.quota;

public class User {

  private String id;
  // TODO: Add a map of quotas?
  
  public User(String user_id) {
    this.id = user_id;
  }
  
  public String getId() {
    return this.id;
  }
  
}
