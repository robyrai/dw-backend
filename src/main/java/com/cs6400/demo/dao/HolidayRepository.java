package com.cs6400.demo.dao;

import com.cs6400.demo.model.Holiday;

import java.util.List;

public interface HolidayRepository {
    void insertHoliday(Holiday h);
    
    List<Holiday> getHoliday();
}
