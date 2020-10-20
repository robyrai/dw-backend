package com.cs6400.demo.serivce;

import com.cs6400.demo.model.City;

import java.util.List;

public interface CityService {
    void updatePopulation(String cityName, String stateName, int newPopulation);
    
    List<City> getCities();
}
