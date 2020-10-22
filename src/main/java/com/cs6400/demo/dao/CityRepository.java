package com.cs6400.demo.dao;

import com.cs6400.demo.model.City;
import java.sql.SQLException;
import java.util.List;

public interface CityRepository {
  void updatePopulation(String cityName, String stateName, int population);

  List<City> getCities() throws SQLException;

}
