package com.cs6400.demo.serivce;

import com.cs6400.demo.dao.StatisticsRepositoryImpl;
import com.cs6400.demo.model.Statistics;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {
  @Autowired
  StatisticsRepositoryImpl repo;

  @Override
  public List<Statistics> getStatistics() throws SQLException {
    return repo.getStatistics();
  }
}
