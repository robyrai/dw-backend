package com.cs6400.demo.controller;

import com.cs6400.demo.model.City;
import com.cs6400.demo.serivce.CityService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/")
public class CityController {
    @Autowired
    private CityService service;
    
    @GetMapping("/population")
    public List<City> getCity() {
        return service.getCities();
    }

    @PutMapping("/population")
    public void updatePopulation(@RequestParam String cityName,
                                 @RequestParam String stateName,
                                 @RequestParam int newPopulation) {
        service.updatePopulation(cityName, stateName, newPopulation);
    }
}
