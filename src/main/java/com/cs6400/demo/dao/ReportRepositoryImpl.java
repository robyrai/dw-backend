package com.cs6400.demo.dao;

import com.cs6400.demo.model.CategoryReport;
import com.cs6400.demo.model.CityMembershipTrend;
import com.cs6400.demo.model.YearMembershipTrend;
import com.cs6400.demo.model.ManufacturerDetail;
import com.cs6400.demo.model.ManufacturerProduct;
import com.cs6400.demo.model.MembershipTrend;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepositoryImpl extends JdbcDaoSupport implements ReportRepository {
  @Autowired
  DataSource dataSource;

  @PostConstruct
  private void initialize() {
    setDataSource(dataSource);
  }

  @Override
  public List<ManufacturerProduct> getManufacturerProduct() throws SQLException {
    String sql = "SELECT m.name, COUNT(p.name), ROUND(avg(p.price), 2) AS average_price, MAX "
                 + "(p.price),  MIN(p.price) FROM product p JOIN manufacturer m ON "
                 + "p.mfg_name=m.name GROUP BY m.name ORDER BY average_price DESC LIMIT 100;";
    List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);
    List<ManufacturerProduct> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      ManufacturerProduct mp = new ManufacturerProduct();
      mp.setName((String) row.get("name"));
      mp.setCount((Long) row.get("count"));
      mp.setAveragePrice((BigDecimal) row.get("average_price"));
      mp.setMaxPrice((Integer) row.get("max"));
      mp.setMinPrice((Integer) row.get("min"));
      result.add(mp);
    }
    getConnection().close();
    return result;
  }

  @Override
  public List<CategoryReport> getCategoryReport() throws SQLException {
    String sql = "SELECT c.name category_name, COUNT(p.productid) COUNT_product, COUNT(DISTINCT"
                 + " p.mfg_name) unique_mfg, ROUND(avg(p.price), 2) average_price FROM "
                 + "product p JOIN product_category_xref x ON p.productid = x.productid JOIN "
                 + "category c ON x.category_name = c.name GROUP BY c.name ORDER BY c.name ASC";
    List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);
    List<CategoryReport> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      CategoryReport cr = new CategoryReport();
      cr.setCategoryName((String) row.get("category_name"));
      cr.setCountProduct((Long) row.get("count_product"));
      cr.setUniqueMfg((Long) row.get("unique_mfg"));
      cr.setAvgPrice((BigDecimal) row.get("average_price"));
      result.add(cr);
    }
    getConnection().close();
    return result;
  }

  @Override
  public List<ManufacturerDetail> getManufacturerDetail(String name) throws SQLException {
    String sql = "SELECT p.productid, p.name product_name, string_agg(c.name, ', ') as "
                 + "category, p.price FROM product p JOIN product_category_xref x ON "
                 + "p.productid = x.productid JOIN category c ON c.name = x.category_name "
                 + "JOIN manufacturer m ON m.name = p.mfg_name WHERE m.name = '" + name + "' "
                 + "GROUP BY p.productid ORDER BY p.price DESC;";
    List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);
    List<ManufacturerDetail> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      ManufacturerDetail md = new ManufacturerDetail();
      md.setProductId((Integer) row.get("productid"));
      md.setName((String) row.get("product_name"));
      md.setCategory((String) row.get("category"));
      md.setPrice((Integer) row.get("price"));
      result.add(md);
    }
    getConnection().close();
    return result;
  }

  @Override
  public int getMfgDiscount(String name) {
    String sql = "SELECT MAX(p.max_discount) FROM manufacturer m, product p where m.name=p.mfg_name AND m.name=? group by m.name;";
    int res = 0;
    ResultSet rs;
    try {
      PreparedStatement ps = getConnection().prepareCall(sql);
      ps.setString(1, name);
      rs = ps.executeQuery();
      if (rs.next()) {
        res = rs.getInt(1);
      }
      rs.close();
      return res;
    } catch (SQLException e) {
      return 0;
    } finally {
      try {
        getConnection().close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public List<MembershipTrend> getMembershipTrend() {
    String sql = "SELECT city, state, signup_year, total, CASE WHEN count > 1 THEN true ELSE " 
                 + "false END " 
                 + "AS isMultiple FROM (SELECT c.name AS city, EXTRACT(YEAR FROM m.signup_date) " 
                 + "AS signup_year,  COUNT(m.signup_date) AS total FROM membership m JOIN store s" 
                 + " ON s.storeid= m.signup_store JOIN city c ON c.storeid=s.storeid GROUP BY " 
                 + "city, signup_year ORDER BY signup_year DESC) t JOIN (select name, state, count" 
                 + "(storeid) from city group by name, state) u ON t.city=u.name;";
    List<MembershipTrend> mtList = new ArrayList<>();
    try {
      PreparedStatement ps = getConnection().prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        MembershipTrend mt = new MembershipTrend();
        mt.setCity(rs.getString("city"));
        mt.setState(rs.getString("state"));
        mt.setYear(rs.getLong("signup_year"));
        mt.setTotal(rs.getLong("total"));
        mt.setMultiple(rs.getBoolean("isMultiple"));
        mtList.add(mt);
      }
      rs.close();
      return mtList;
    } catch (SQLException e) {
      return null;
    } finally {
      try {
        getConnection().close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public List<YearMembershipTrend> getYearMembershipTrend(long year) {
    String sql = "SELECT c.name AS CITY, c.state AS state, EXTRACT(YEAR FROM m.signup_date) "
                 + "AS signup_year,  COUNT(m.signup_date) AS total FROM membership m JOIN "
                 + "store s ON s.storeid= m.signup_store JOIN city c ON c.storeid=s.storeid "
                 + "WHERE EXTRACT(YEAR FROM m.signup_date)=? GROUP BY city, state, "
                 + "signup_year ORDER BY total DESC FETCH FIRST 3 ROWS ONLY";

    List<YearMembershipTrend> ymList = new ArrayList<>();
    try {
      PreparedStatement ps = getConnection().prepareCall(sql);
      ps.setLong(1, year);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        YearMembershipTrend ym = new YearMembershipTrend();
        ym.setCity(rs.getString("city"));
        ym.setState(rs.getString("state"));
        ym.setSignupYear(rs.getDouble("signup_year"));
        ym.setTotal(rs.getLong("total"));
        ymList.add(ym);
      }
      rs.close();
      return ymList;
    } catch (SQLException e) {
      return null;
    } finally {
      try {
        getConnection().close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public List<CityMembershipTrend> getCityMembershipTrend(String city, String state, long year) {
    String sql = "SELECT c.storeid, s.address, c.name, COUNT(m.mid) AS total_membership FROM city c JOIN store s ON s.storeid=c.storeid JOIN membership m ON m.signup_store=s.storeid WHERE c.name=? AND c.state=? AND EXTRACT(YEAR FROM m.signup_date)=? GROUP BY c.storeid, s.address, c.name;\n";

    List<CityMembershipTrend> cmList = new ArrayList<>();
    try {
      PreparedStatement ps = getConnection().prepareCall(sql);
      ps.setString(1, city);
      ps.setString(2, state);
      ps.setLong(3, year);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        CityMembershipTrend cm = new CityMembershipTrend();
        cm.setStoreId(rs.getInt("storeid"));
        cm.setAddress(rs.getString("address"));
        cm.setCity(rs.getString("name"));
        cm.setCount(rs.getLong("total_membership"));
        cmList.add(cm);
      }
      rs.close();
      return cmList;
    } catch (SQLException e) {
      return null;
    } finally {
      try {
        getConnection().close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
