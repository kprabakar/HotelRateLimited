package com.hotel;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class Config {
  public static final String GLOBAL_RATE_LIMIT = "global.ratelimit";
  public static final String GLOBAL_RATE_LIMIT_OPEN_WINDOW = "global.ratelimit.open.window";
  public static final String GLOBAL_RATE_LIMIT_CLOSE_WINDOW = "global.ratelimit.close.window";

  private static final Properties properties = new Properties();

  static {
    try {
      properties.load(Config.class.getClass().getResourceAsStream("/config.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String getProperty(String key) {
    return properties.getProperty(key);
  }

  public static Set<Object> allKeys() {
    return properties.keySet();
  }
}
