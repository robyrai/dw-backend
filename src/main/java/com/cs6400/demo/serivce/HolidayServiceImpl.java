package com.cs6400.demo.serivce;

import com.cs6400.demo.dao.HolidayRepository;
import com.cs6400.demo.model.Holiday;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HolidayServiceImpl implements HolidayService {
    @Autowired
    private HolidayRepository repo;
    
    @Override
    public void insertHoliday(Holiday h) {
        repo.insertHoliday(h);
    }

    @Override
    public List<Holiday> getHolidays() {
        return repo.getHoliday();
    }
}
