package com.cs6400.demo.controller;

import com.cs6400.demo.model.Holiday;
import com.cs6400.demo.serivce.HolidayService;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:3000")
public class HolidayController {
  @Autowired
  HolidayService service;

  @GetMapping("/holidays")
  public List<Holiday> getHolidays() throws SQLException {
    return service.getHolidays();
  }

  @PostMapping("/holidays")
  public void addHoliday(@RequestBody Holiday h) {
    service.insertHoliday(h);
  }
}
