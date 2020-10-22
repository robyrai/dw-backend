package com.cs6400.demo.dao;

import com.cs6400.demo.model.Statistics;
import java.sql.SQLException;
import java.util.List;

public interface StatisticsRepository {
  List<Statistics> getStatistics() throws SQLException;
}
