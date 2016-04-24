package com.hotel.model;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class Hotel {
  public static final String HOTEL_NAME = "hotelName";
  public static final String HOTEL_ID = "hotelId";
  public static final String CITY_NAME = "cityName";
  public static final String CITY_ID = "cityId";
  public static final String PRICE = "price";

  public String hotelName;
  public String hotelId;
  public String cityName;
  public String cityId;
  public double price;

  @Override
  public String toString() {
    return String.format("{%s : %s, %s : %s, %s : %s, %s : %s, %s : %s}",
        HOTEL_NAME, hotelName,
        HOTEL_ID, hotelId,
        CITY_NAME, cityName,
        CITY_ID, cityId,
        PRICE, price
        );
  }
}
