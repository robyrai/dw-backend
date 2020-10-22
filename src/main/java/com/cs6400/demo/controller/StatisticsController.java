package com.cs6400.demo.controller;

import com.cs6400.demo.model.Statistics;
import com.cs6400.demo.serivce.StatisticsServiceImpl;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:3000")
public class StatisticsController {
  @Autowired
  StatisticsServiceImpl service;

  @GetMapping("/hello")
  public String hello() {
    return "Hello CS6400!";
  }

  @GetMapping("/statistics")
  public List<Statistics> getStatistics() throws SQLException {
    return service.getStatistics();
  }
}
