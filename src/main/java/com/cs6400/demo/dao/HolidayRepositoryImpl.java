package com.cs6400.demo.dao;

import com.cs6400.demo.model.Holiday;
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
public class HolidayRepositoryImpl implements HolidayRepository {
  @Autowired
  @Qualifier("ppdw-jdbc-template")
  JdbcTemplate ppJdbcTemplate;

  @Override
  public void insertHoliday(Holiday h) {
    String sql =
        String.format("INSERT INTO Holiday (Date, Name) VALUES ('%s', '%s')", h.getDate(),
            h.getName());
    ppJdbcTemplate.execute(sql);
  }

  @Override
  public List<Holiday> getHoliday() throws SQLException {
    List<Map<String, Object>> rows = ppJdbcTemplate.queryForList("SELECT * FROM holiday");
    List<Holiday> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      Holiday h = new Holiday();
      java.sql.Date d = (java.sql.Date) row.get("date");
      h.setDate(d.toLocalDate());
      h.setName((String) row.get("name"));
      result.add(h);
    }
    return result;
  }
}
