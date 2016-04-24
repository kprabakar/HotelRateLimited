package com.hotel.service;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public interface RateLimitCache {

  /**
   * Says whether API is accessible or suspended. Calling this method increments api access count
   * by 1 when limit not reached.
   * @param apiKey - user auth key
   * @return - returns true if the caller can proceed to access the api, false otherwise
   */
  boolean getAccess(String apiKey);

  /**
   * Configures rate limit for an apiKey
   * @param apiKey
   * @param limit - maximum no.of api access count allowed for given openWindow
   * @param openWindow - time duration for which api access count is limited in seconds
   * @param closeWindow - api suspend duration when throttle limit is exceeded in seconds
   */
  void setConfig(String apiKey, long limit, int openWindow, int closeWindow);

  /**
   *
   * @param apiKey
   * @return - returns the limit value set for the apiKey
   */
  long getLimit(String apiKey);

  /**
   *
   * @param apiKey
   * @return - no.of times api was accessed by apiKey user in the current 10 second window
   */
  long getCurrentAccessCount(String apiKey);

  long getSuspensionWindow(String apiKey);
}
