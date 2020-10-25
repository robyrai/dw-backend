package com.cs6400.demo.dao;

import com.cs6400.demo.model.City;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CityRepositoryImpl implements CityRepository {
  @Autowired
  @Qualifier("ppdw-jdbc-template")
  JdbcTemplate ppJdbcTemplate;

  @Autowired
  @Qualifier("ppdw-connection")
  Connection ppConnxn;

  @Override
  public void updatePopulation(String cityName, String stateName, int newPop) {
    String sql = String.format("UPDATE CITY SET population=%d WHERE name='%s' AND state='%s'",
        newPop, cityName, stateName);
    ppJdbcTemplate.execute(sql);
  }

  @Override
  public List<City> getCities() throws SQLException {
    List<Map<String, Object>> rows = ppJdbcTemplate.queryForList("select distinct name, state, population from city;");
    List<City> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      City h = new City();
      h.setCityName((String) row.get("name"));
      h.setStateName((String) row.get("state"));
      h.setPopulation((Integer) row.get("population"));
      result.add(h);
    }
    ppConnxn.close();
    return result;
  }
}
