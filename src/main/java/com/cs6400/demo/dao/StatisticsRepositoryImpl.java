package com.cs6400.demo.dao;

import com.cs6400.demo.model.Statistics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class StatisticsRepositoryImpl implements StatisticsRepository {

  @Autowired
  @Qualifier("ppdw-jdbc-template")
  JdbcTemplate ppJdbcTemplate;

  @Override
  public List<Statistics> getStatistics() throws SQLException {
    String sql = "SELECT relname AS field, n_live_tup AS total FROM pg_stat_user_tables "
                 + "WHERE schemaname = 'cs6400' AND relname IN ('product', 'store', "
                 + "'membership', 'manufacturer')";
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
