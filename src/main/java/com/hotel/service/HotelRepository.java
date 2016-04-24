package com.hotel.service;

import com.hotel.model.Hotel;

import java.util.List;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public interface HotelRepository {
  List<Hotel> getHotelsByCityId(String cityId);
  List<Hotel> getAllHotels();
}
