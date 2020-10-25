package com.cs6400.demo.dao;

import com.cs6400.demo.model.Statistics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StatisticsRepositoryImpl implements StatisticsRepository {
  // Using Jdbc template to try. But best practice is to get a connection and use Prepared 
  // Statements
  @Autowired
  @Qualifier("ppdw-jdbc-template")
  JdbcTemplate ppJdbcTemplate;

  @Override
  public List<Statistics> getStatistics() throws SQLException {
    String sql = "SELECT 'store' AS table_name, COUNT(1) FROM Store "
                 + "UNION "
                 + "SELECT 'manufacturer' AS table_name, COUNT(1) FROM Manufacturer "
                 + "UNION "
                 + "SELECT 'product' AS table_name, COUNT(1) FROM Product "
                 + "UNION "
                 + "SELECT 'membership' AS table_name, COUNT(1) FROM Membership";
    List<Map<String, Object>> rows = ppJdbcTemplate.queryForList(sql);
    List<Statistics> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      Statistics st = new Statistics();
      st.setField((String) row.get("field"));
      st.setTotal((Long) row.get("total"));
      result.add(st);
    }
    return result;
  }
}
