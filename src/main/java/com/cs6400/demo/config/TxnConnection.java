package com.cs6400.demo.config;

import java.sql.Connection;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

@Configuration
public class TxnConnection extends JdbcDaoSupport {

  @Autowired
  private DataSource dataSource;

  @PostConstruct
  private void initialize() {
    setDataSource(dataSource);
  }
  
  @Bean("ppdw-jdbc-template")
  JdbcTemplate ppConxn() {
    return getJdbcTemplate();
  }
  
  @Bean("ppdw-connection")
  Connection getPPConnection() {
    return getConnection();
  }
  
}
