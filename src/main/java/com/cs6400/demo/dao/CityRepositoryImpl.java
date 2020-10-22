package com.cs6400.demo.dao;

import com.cs6400.demo.model.City;
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
public class CityRepositoryImpl extends JdbcDaoSupport implements CityRepository {

  @Autowired
  private DataSource dataSource;

  @PostConstruct
  private void initialize() {
    setDataSource(dataSource);
  }

  @Override
  public void updatePopulation(String cityName, String stateName, int newPop) {
    String sql = String.format("UPDATE CITY SET population=%d WHERE name='%s' AND state='%s'",
        newPop, cityName, stateName);
    getJdbcTemplate().execute(sql);
  }

  @Override
  public List<City> getCities() throws SQLException {
    List<Map<String, Object>> rows = getJdbcTemplate().queryForList("select distinct name, state, population from city;");
    List<City> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      City h = new City();
      h.setCityName((String) row.get("name"));
      h.setStateName((String) row.get("state"));
      h.setPopulation((Integer) row.get("population"));
      result.add(h);
    }
    getConnection().close();
    return result;
  }
}
