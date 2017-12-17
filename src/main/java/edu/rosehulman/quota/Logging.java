package edu.rosehulman.quota;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logging {
  private static final Logger errorLog = LogManager.getLogger("errorLog");

  public static void errorLog(String message) {
    errorLog.error(message);
  }

  public static void errorLog(Exception e) {
    StringWriter sw = new StringWriter();
    e.printStackTrace(new PrintWriter(sw));
    String sStackTrace = sw.toString(); // stack trace as a string
    errorLog.error(sStackTrace);
  }

}
