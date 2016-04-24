package com.hotel.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is a singleton class.
 *
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public final class RateLimitCacheImpl implements RateLimitCache, Cloneable {
  private static RateLimitCacheImpl instance;

  private Map<String, Throttle> throttleCache = new HashMap<>();
  private Map<String, ThrottleConfig> configCache = new HashMap<>();
  private final ReentrantLock lock = new ReentrantLock();

  private final long GLOBAL_LIMIT;
  private final int GLOBAL_OPEN_WINDOW;
  private final int GLOBAL_CLOSE_WINDOW;

  private RateLimitCacheImpl(long globalLimit, int globalOpenWindow, int globalCloseWindow) {
    this.GLOBAL_LIMIT = globalLimit;
    this.GLOBAL_OPEN_WINDOW = globalOpenWindow;
    this.GLOBAL_CLOSE_WINDOW = globalCloseWindow;
  }

  public static synchronized RateLimitCacheImpl getInstance(long globalLimit, int globalOpenWindow,
      int globalCloseWindow) {
    instance = new RateLimitCacheImpl(globalLimit, globalOpenWindow, globalCloseWindow);
    return instance;
  }

  @Override
  public boolean getAccess(String apiKey) {
    Throttle throttle = throttleCache.get(apiKey);
    ThrottleConfig config = configCache.get(apiKey);
    lock.lock();
    try {
      if (config == null) {
        config = getGlobalConfig();
        configCache.put(apiKey, config);
      }
      if (throttle == null || throttle.expiry < System.currentTimeMillis()) {
        if (throttle == null) {
          throttle = new Throttle();
        } else {
          throttle.state = ThrottleState.OPEN;
          throttle.count = 0;
        }
        throttle.expiry = System.currentTimeMillis() + config.OPEN_WINDOW * 1000;
        throttleCache.put(apiKey, throttle);
      }

      if (throttle.state == ThrottleState.OPEN) {
        throttle.count++;
        if (throttle.count <= config.LIMIT) {
          return true;
        } else { //limit exceeded
          throttle.state = ThrottleState.CLOSE;
          throttle.expiry = System.currentTimeMillis() + config.CLOSE_WINDOW * 1000;
          return false;
        }
      }
    } finally {
      lock.unlock();
    }
    return false;
  }


  @Override
  public void setConfig(String apiKey, long limit, int openDuration, int closeDuration) {
    ThrottleConfig throttle = new ThrottleConfig();
    throttle.LIMIT = limit;
    throttle.OPEN_WINDOW = openDuration;
    throttle.CLOSE_WINDOW = closeDuration;
    lock.lock();
    try {
      configCache.put(apiKey, throttle);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public long getLimit(String apiKey) {
    if (configCache.containsKey(apiKey)) {
      return configCache.get(apiKey).LIMIT;
    }
    return 0;
  }

  @Override
  public long getCurrentAccessCount(String apiKey) {
    if (throttleCache.containsKey(apiKey)) {
      return throttleCache.get(apiKey).count;
    }
    return 0;
  }

  @Override
  public long getSuspensionWindow(String apiKey) {
    if(!configCache.containsKey(apiKey)) {
      return GLOBAL_CLOSE_WINDOW;
    }
    return configCache.get(apiKey).CLOSE_WINDOW;
  }

  public Object clone() {
    return instance;
  }

  private ThrottleConfig getGlobalConfig() {
    ThrottleConfig config = new ThrottleConfig();
    config.LIMIT = GLOBAL_LIMIT;
    config.OPEN_WINDOW = GLOBAL_OPEN_WINDOW;
    config.CLOSE_WINDOW = GLOBAL_CLOSE_WINDOW;
    return config;
  }

  static enum ThrottleState {
    OPEN, CLOSE;
  }

  static class ThrottleConfig {
    long LIMIT;
    int OPEN_WINDOW;
    int CLOSE_WINDOW;
  }

  static class Throttle {
    ThrottleState state = ThrottleState.OPEN;
    long count;
    long expiry; //in millis
  }
}
