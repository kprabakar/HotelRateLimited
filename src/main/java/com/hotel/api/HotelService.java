package com.hotel.api;

import com.hotel.api.error.ApiAccessLimitExceededException;
import com.hotel.model.Hotel;
import com.hotel.service.HotelRepository;
import com.hotel.service.RateLimitCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class HotelService {
  private HotelRepository hotelRepository;

  private RateLimitCache rateLimitCache;

  public List<Hotel> searchHotels(String apiKey, String byCityId, String sortColumn, Sort order)
      throws ApiAccessLimitExceededException {
    if (!rateLimitCache.getAccess(apiKey)) {
      throw new ApiAccessLimitExceededException(
          rateLimitCache.getSuspensionWindow(apiKey) / 60.0 + "mins");
    }
    List<Hotel> resultList = new ArrayList<>();
    resultList = hotelRepository.getHotelsByCityId(byCityId);
    if (Hotel.PRICE.equalsIgnoreCase(sortColumn)) {
      Collections.sort(resultList, new Comparator<Hotel>() {
        @Override
        public int compare(Hotel o1, Hotel o2) {
          if (order == Sort.ASC) {
            return (int) (o1.price - o2.price);
          } else {
            return (int) (o2.price - o1.price);
          }
        }
      });
    }

    return resultList;
  }

  public void setRateLimitCache(RateLimitCache rateLimitCache) {
    this.rateLimitCache = rateLimitCache;
  }

  public void setHotelRepository(HotelRepository hotelRepository) {
    this.hotelRepository = hotelRepository;
  }

  public static enum Sort {
    ASC, DESC
  }
}
