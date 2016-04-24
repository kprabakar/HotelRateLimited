package com.hotel.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.UUID;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class RateLimitCacheTest {
  private RateLimitCacheImpl cache = RateLimitCacheImpl.getInstance(2, 1, 5);
  private String apiKey = UUID.randomUUID().toString();

  @Test
  public void shouldGetAccessOnFirstAttempt() {
    RateLimitCacheImpl cache = RateLimitCacheImpl.getInstance(2, 1, 5);
    assertTrue(cache.getAccess(apiKey));
  }

  @Test
  public void shouldNotGetAccessAfterLimit() {
    RateLimitCacheImpl cache = RateLimitCacheImpl.getInstance(2, 1, 5);
    assertTrue(cache.getAccess(apiKey));
    assertTrue(cache.getAccess(apiKey));
    assertFalse(cache.getAccess(apiKey));
  }

  @Test
  public void shouldGetAccessInNextWindow() throws InterruptedException {
    RateLimitCacheImpl cache = RateLimitCacheImpl.getInstance(2, 1, 5);
    assertTrue(cache.getAccess(apiKey));
    assertTrue(cache.getAccess(apiKey));
    Thread.sleep(1000);
    assertTrue(cache.getAccess(apiKey));
  }

  @Test
  public void shouldNotGetAccessAfterLimitExceededForTheEntireSuspensionPeriod()
      throws InterruptedException {
    RateLimitCacheImpl cache = RateLimitCacheImpl.getInstance(2, 1, 2);
    assertTrue(cache.getAccess(apiKey));
    assertTrue(cache.getAccess(apiKey));
    assertFalse(cache.getAccess(apiKey));
    long limit = System.currentTimeMillis() + 2 * 1000;
    long time = System.currentTimeMillis();
    while (time < limit) {
      assertFalse(cache.getAccess(apiKey));
      Thread.sleep(50);
      time = System.currentTimeMillis();
    }
    assertTrue(cache.getAccess(apiKey));
  }

  @Test
  public void shouldSetConfigLimitCorrectly() {
    RateLimitCacheImpl cache = RateLimitCacheImpl.getInstance(2, 1, 2);
    cache.setConfig(apiKey, 7, 1, 1);
    assertEquals(7, cache.getLimit(apiKey));
  }

  @Test
  public void shouldNotThrowExceptionWhenGettingLimitForUnconfiguredApiKey() {
    RateLimitCacheImpl cache = RateLimitCacheImpl.getInstance(2, 1, 2);
    assertEquals(0, cache.getLimit(apiKey));
  }

  @Test
  public void shouldCorrectAccessCount() {
    RateLimitCacheImpl cache = RateLimitCacheImpl.getInstance(3, 1, 2);
    cache.getAccess(apiKey);
    assertEquals(1, cache.getCurrentAccessCount(apiKey));
    cache.getAccess(apiKey);
    assertEquals(2, cache.getCurrentAccessCount(apiKey));
    cache.getAccess(apiKey);
    assertEquals(3, cache.getCurrentAccessCount(apiKey));
  }

  @Test
  public void shouldReturnTheSameObjectWhenCloned() {
    RateLimitCacheImpl cache = RateLimitCacheImpl.getInstance(2, 1, 2);
    assertEquals(cache, cache.clone());
  }

}
