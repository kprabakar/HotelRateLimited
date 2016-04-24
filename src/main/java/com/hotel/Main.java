package com.hotel;

import com.hotel.api.HotelService;
import com.hotel.api.error.ApiAccessLimitExceededException;
import com.hotel.model.Hotel;
import com.hotel.service.HotelRepository;
import com.hotel.service.HotelRepositoryImpl;
import com.hotel.service.RateLimitCache;
import com.hotel.service.RateLimitCacheImpl;

import java.util.List;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class Main {
  static long globalRateLimit = Long.valueOf(Config.getProperty(Config.GLOBAL_RATE_LIMIT));
  static int globalOpenWindow =
      Integer.valueOf(Config.getProperty(Config.GLOBAL_RATE_LIMIT_OPEN_WINDOW));
  static int globalCloseWindow =
      Integer.valueOf(Config.getProperty(Config.GLOBAL_RATE_LIMIT_CLOSE_WINDOW));

  public static void main(String[] args) throws InterruptedException {
    RateLimitCache rateLimitCache = RateLimitCacheImpl.getInstance(globalRateLimit,
        globalOpenWindow, globalCloseWindow);
    loadApiKeyConfig(rateLimitCache);
    HotelRepository repository = new HotelRepositoryImpl("src/main/resources");
    HotelService hotelService = new HotelService();
    hotelService.setHotelRepository(repository);
    hotelService.setRateLimitCache(rateLimitCache);
    accessApi(hotelService);
    accessApi(hotelService);
    System.out.println("Waiting for suspension timeout ....");
    Thread.sleep(globalCloseWindow * 1000);
    System.out.println("Trying the API again...");
    accessApi(hotelService);
  }

  private static void accessApi(HotelService hotelService) {
    try {
      List<Hotel> list = hotelService.searchHotels("meme", "1", "price", HotelService.Sort.ASC);
      System.out.println("Hotels search result: " + list);
    } catch (ApiAccessLimitExceededException e) {
      System.out.println(e.getMessage());
    }
  }

  private static void loadApiKeyConfig(RateLimitCache rateLimitCache) {
    Config.allKeys().stream().map(k -> (String) k).forEach(key -> {
      if (key.startsWith("ratelimit.apiKey")) {
        rateLimitCache.setConfig(key.split("\\.")[2], Long.valueOf(Config
                .getProperty(key)),
            globalOpenWindow, globalCloseWindow);
      }
    });
  }
}
