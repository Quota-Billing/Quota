package edu.rosehulman.quota;

import java.math.BigInteger;

public class TimeParser implements Parser {

  @Override
  public String parse(String value) {
    String lowerValue = value.toLowerCase();
    // Format is 1yr6mo15d5hr10m30s

    String[] identifiers = lowerValue.split("[0-9]+");
    String[] nums = lowerValue.split("[a-z]+");
    String identifier;
    String seconds = "1";
    for (int i = 0; i < nums.length; i++) {
      identifier = identifiers[i + 1];
      if ("yr".equals(identifier)) {
        String product = new BigInteger(nums[i]).multiply(new BigInteger("31536000")).toString();
        seconds = new BigInteger(seconds).add(new BigInteger(product)).toString();
      } else if ("mo".equals(identifier)) {
        String product = new BigInteger(nums[i]).multiply(new BigInteger("2628000")).toString();
        seconds = new BigInteger(seconds).add(new BigInteger(product)).toString();
      } else if ("d".equals(identifier)) {
        String product = new BigInteger(nums[i]).multiply(new BigInteger("86400")).toString();
        seconds = new BigInteger(seconds).add(new BigInteger(product)).toString();
      } else if ("hr".equals(identifier)) {
        String product = new BigInteger(nums[i]).multiply(new BigInteger("3600")).toString();
        seconds = new BigInteger(seconds).add(new BigInteger(product)).toString();
      } else if ("m".equals(identifier)) {
        String product = new BigInteger(nums[i]).multiply(new BigInteger("60")).toString();
        seconds = new BigInteger(seconds).add(new BigInteger(product)).toString();
      } else if ("s".equals(identifier)) {
        seconds = new BigInteger(seconds).add(new BigInteger(nums[i])).toString();
      }
    }
    return seconds;
  }
}
