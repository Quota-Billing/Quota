package edu.rosehulman.quota;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;

import edu.rosehulman.quota.model.Config;

public class SystemConfig {

  private static SystemConfig instance;
  private Config config;

  public SystemConfig() {
    Gson gson = new Gson();

    try (Reader reader = new FileReader("/quota/config.json")) {
      config = gson.fromJson(reader, Config.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static synchronized SystemConfig getInstance() throws Exception {
    if (instance == null) {
      instance = new SystemConfig();
    }
    return instance;
  }

  public String getDatabaseUrl() {
    return config.getDatabaseUrl();
  }

  public String getSharedServerPath() {
    return config.getSharedServerPath();
  }

  public String getBillingServerPath() {
    return config.getBillingServerPath();
  }
}
