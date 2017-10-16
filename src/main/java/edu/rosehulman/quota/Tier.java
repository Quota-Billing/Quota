package edu.rosehulman.quota;

public class Tier {
  private String id;
  private String name;
  private int max; // TODO: This might need to be changed to BigInt or BigDouble
  private int value;
  private double price; // TODO: Not a good idea to save price in a double format

  public Tier(String id, String name, int max, double price) {
    this.id = id;
    this.name = name;
    this.max = max;
    this.price = price;
  }

  public Tier() {
  }

  public Tier(Tier copyTier) {
    this.id = copyTier.getId();
    this.name = copyTier.getName();
    this.max = copyTier.getMax();
    this.value = 0; // Default 0?
    this.price = copyTier.getPrice();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }
}
