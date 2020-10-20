package com.cs6400.demo.serivce;

import com.cs6400.demo.model.Holiday;

import java.util.List;

public interface HolidayService {
    void insertHoliday(Holiday h);
    
    List<Holiday> getHolidays();
}
