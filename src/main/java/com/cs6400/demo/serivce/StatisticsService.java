package com.cs6400.demo.serivce;

import com.cs6400.demo.model.Statistics;
import java.sql.SQLException;
import java.util.List;

public interface StatisticsService {
  List<Statistics> getStatistics() throws SQLException;
}
