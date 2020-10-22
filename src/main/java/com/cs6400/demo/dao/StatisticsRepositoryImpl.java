package com.cs6400.demo.dao;

import com.cs6400.demo.model.Statistics;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class StatisticsRepositoryImpl extends JdbcDaoSupport implements StatisticsRepository {

  @Autowired
  DataSource dataSource;

  @PostConstruct
  private void initialize() {
    setDataSource(dataSource);
  }

  @Override
  public List<Statistics> getStatistics() throws SQLException {
    String sql = "SELECT relname AS field, n_live_tup AS total FROM pg_stat_user_tables "
                 + "WHERE schemaname = 'cs6400' AND relname IN ('product', 'store', "
                 + "'membership', 'manufacturer')";
    List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);
    List<Statistics> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      Statistics st = new Statistics();
      st.setField((String) row.get("field"));
      st.setTotal((Long) row.get("total"));
      result.add(st);
    }
    getConnection().close();
    return result;
  }
}
