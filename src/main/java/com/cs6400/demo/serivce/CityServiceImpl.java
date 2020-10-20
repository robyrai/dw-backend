package com.cs6400.demo.serivce;

import com.cs6400.demo.dao.CityRepository;
import com.cs6400.demo.model.City;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityServiceImpl implements CityService {
    @Autowired
    private CityRepository repo;
    
    @Override
    public void updatePopulation(String cityName, String stateName, int newPopulation) {
        repo.updatePopulation(cityName, stateName, newPopulation);
    }

    @Override
    public List<City> getCities() {
        return repo.getCities();
    }
}
