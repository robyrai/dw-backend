package com.cs6400.demo.dao;

import com.cs6400.demo.model.Statistics;

import java.util.List;

public interface StatisticsRepository {
    List<Statistics> getStatistics();
}
