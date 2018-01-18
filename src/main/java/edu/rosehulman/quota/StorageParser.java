package edu.rosehulman.quota;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageParser implements Parser {

  @Override
  public String parse(String value) {
    String lowerValue = value.toLowerCase();
    final Pattern pattern = Pattern.compile("\\d+");
    final Matcher matcher = pattern.matcher(lowerValue);
    matcher.find();
    String num = matcher.group();
    if (lowerValue.endsWith("gb")) {
      return (new BigInteger(num).multiply(new BigInteger("1073741824"))).toString();
    } else if (lowerValue.endsWith("mb")) {
      return (new BigInteger(num).multiply(new BigInteger("1048576"))).toString();
    } else if (lowerValue.endsWith("kb")) {
      return (new BigInteger(num).multiply(new BigInteger("1024"))).toString();
    } else if (lowerValue.endsWith("tb")) {
      return (new BigInteger(num).multiply(new BigInteger("1099511627776"))).toString();
    }
    // They already had it in bytes
    return num;
  }
}
