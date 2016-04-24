package com.hotel.service;

import com.hotel.model.Hotel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Prabakar Kalivaradan (Prabakar_Kalivaradan@Trimble.com)
 */
public class HotelRepositoryImpl implements HotelRepository {
  private String csvFilePath;

  public static void main(String[] args) {
    new HotelRepositoryImpl("src/main/resources");
  }

  public HotelRepositoryImpl(String csvFilePath) {
    this.csvFilePath = csvFilePath;
    initCsv();
  }

  @Override
  public List<Hotel> getHotelsByCityId(String cityId) {
    ResultSet resultSet = execute(String.format("SELECT * FROM hotels where cityId='%s'", cityId));
    return parseResultSet(resultSet);
  }

  @Override
  public List<Hotel> getAllHotels() {
    ResultSet resultSet = execute("SELECT * FROM hotels");
    return parseResultSet(resultSet);
  }

  private List<Hotel> parseResultSet(ResultSet resultSet) {
    if (resultSet == null) {
      return Collections.EMPTY_LIST;
    }
    List<Hotel> list = new ArrayList<>();
    try {
      while (resultSet.next()) {
        Hotel hotel = new Hotel();
        hotel.hotelName = resultSet.getString(Hotel.HOTEL_NAME);
        hotel.hotelId = resultSet.getString(Hotel.HOTEL_ID);
        hotel.cityName = resultSet.getString(Hotel.CITY_NAME);
        hotel.cityId = resultSet.getString(Hotel.CITY_ID);
        hotel.price = Double.valueOf(resultSet.getString(Hotel.PRICE));
        list.add(hotel);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  private ResultSet execute(String sql) {
    try {
      Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + this.csvFilePath);
      Statement stmt = conn.createStatement();
      return stmt.executeQuery(sql);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private void initCsv() {
    try {
      Class.forName("org.relique.jdbc.csv.CsvDriver");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
