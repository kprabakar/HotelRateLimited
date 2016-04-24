package com.hotel.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.hotel.model.Hotel;
import org.junit.Test;

import java.util.List;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class HotelRepositoryImplTest {
  private HotelRepositoryImpl repository = new HotelRepositoryImpl("src/main/resources");

  @Test
  public void shouldReturnNonEmptyRecordsFromCsv() {
    List<Hotel> hotels = repository.getAllHotels();
    assertNotNull(hotels);
    assertFalse(hotels.isEmpty());
  }
}
