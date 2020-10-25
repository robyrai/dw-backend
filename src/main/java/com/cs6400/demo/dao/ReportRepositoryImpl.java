package com.cs6400.demo.dao;

import com.cs6400.demo.model.CategoryReport;
import com.cs6400.demo.model.CityMembershipTrend;
import com.cs6400.demo.model.GpsPrediction;
import com.cs6400.demo.model.GroundhogDayReport;
import com.cs6400.demo.model.HighestVolumeCateogry;
import com.cs6400.demo.model.ManufacturerDetail;
import com.cs6400.demo.model.ManufacturerProduct;
import com.cs6400.demo.model.MembershipTrend;
import com.cs6400.demo.model.RevenuePopulation;
import com.cs6400.demo.model.StoreRevenueByStateByYear;
import com.cs6400.demo.model.YearMembershipTrend;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepositoryImpl implements ReportRepository {

  @Autowired
  @Qualifier("ppdw-jdbc-template")
  JdbcTemplate ppJdbcTemplate;

  @Autowired
  @Qualifier("ppdw-connection")
  Connection ppConnxn;

  @Override
  public List<ManufacturerProduct> getManufacturerProduct() throws SQLException {
    String sql = "SELECT m.name, COUNT(p.name), ROUND(avg(p.price), 2) AS average_price, MAX "
                 + "(p.price),  MIN(p.price) FROM product p JOIN manufacturer m ON "
                 + "p.mfg_name=m.name GROUP BY m.name ORDER BY average_price DESC LIMIT 100;";
    List<Map<String, Object>> rows = ppJdbcTemplate.queryForList(sql);
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
    return result;
  }

  @Override
  public List<CategoryReport> getCategoryReport() throws SQLException {
    String sql = "SELECT c.name category_name, COUNT(p.productid) COUNT_product, COUNT(DISTINCT"
                 + " p.mfg_name) unique_mfg, ROUND(avg(p.price), 2) average_price FROM "
                 + "product p JOIN product_category_xref x ON p.productid = x.productid JOIN "
                 + "category c ON x.category_name = c.name GROUP BY c.name ORDER BY c.name ASC";
    List<Map<String, Object>> rows = ppJdbcTemplate.queryForList(sql);
    List<CategoryReport> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      CategoryReport cr = new CategoryReport();
      cr.setCategoryName((String) row.get("category_name"));
      cr.setCountProduct((Long) row.get("count_product"));
      cr.setUniqueMfg((Long) row.get("unique_mfg"));
      cr.setAvgPrice((BigDecimal) row.get("average_price"));
      result.add(cr);
    }
    return result;
  }

  @Override
  public List<ManufacturerDetail> getManufacturerDetail(String name) throws SQLException {
    String sql = "SELECT p.productid, p.name product_name, string_agg(c.name, ', ') as "
                 + "category, p.price FROM product p JOIN product_category_xref x ON "
                 + "p.productid = x.productid JOIN category c ON c.name = x.category_name "
                 + "JOIN manufacturer m ON m.name = p.mfg_name WHERE m.name = '" + name + "' "
                 + "GROUP BY p.productid ORDER BY p.price DESC;";
    List<Map<String, Object>> rows = ppJdbcTemplate.queryForList(sql);
    List<ManufacturerDetail> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      ManufacturerDetail md = new ManufacturerDetail();
      md.setProductId((Integer) row.get("productid"));
      md.setName((String) row.get("product_name"));
      md.setCategory((String) row.get("category"));
      md.setPrice((Integer) row.get("price"));
      result.add(md);
    }
    return result;
  }

  @Override
  public int getMfgDiscount(String name) {
    String sql = "SELECT MAX(p.max_discount) FROM manufacturer m, product p where m.name=p.mfg_name AND m.name=? group by m.name;";
    int res = 0;
    ResultSet rs;
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ps.setString(1, name);
      rs = ps.executeQuery();
      if (rs.next()) {
        res = rs.getInt(1);
      }
      rs.close();
      return res;
    } catch (SQLException e) {
      return 0;
    }
  }

  @Override
  public List<GpsPrediction> getGpsPrediction() {
    String sql = "Select * from store;";
    List<GpsPrediction> gpList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        GpsPrediction gp = new GpsPrediction();
        gp.setProductId(rs.getInt("productId"));
        gp.setName(rs.getString("name"));
        gp.setRetailPrice(rs.getInt("retail_price"));
        gp.setUnitsSold(rs.getLong("units_sold"));
        gp.setUnitsSoldOnDiscount(rs.getLong("units_sold_on_discount"));
        gp.setUnitsSoldAtRetailPrice(rs.getLong("units_sold_at_retail_price"));
        gp.setActualRevenue(rs.getLong("actual_revenue"));
        gp.setPredictedRevenue(rs.getLong("predicted_revenue"));
        gp.setDifference(rs.getLong("difference"));
        gpList.add(gp);
      }
      rs.close();
      return gpList;
    } catch (SQLException e) {
      return null;
    }
  }

  @Override
  public List<RevenuePopulation> getRevenueByPopulation() {
    String sql = "SELECT t.category, t.txn_year, t.revenue FROM (SELECT CASE WHEN c.population > " 
                 + "700000 THEN 'maximus' WHEN c.population > 500000 THEN 'medium' WHEN c.population > 300000 THEN 'minimum' ELSE 'itsybitsy' END as category, CASE WHEN c.population > 700000 THEN 3 WHEN c.population > 500000 THEN 2 WHEN c.population > 300000 THEN 1 ELSE 0 END as rnk, EXTRACT(YEAR FROM d.date) AS txn_year, round(SUM(d.price)/COUNT(DISTINCT(c.name)), 2) AS revenue FROM sold d JOIN store s ON s.storeid=d.storeid JOIN city c ON c.storeid=s.storeid GROUP BY category, txn_year, rnk ORDER BY txn_year ASC, rnk ASC) as t;";
    List<RevenuePopulation> rpList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        RevenuePopulation rp = new RevenuePopulation();
        rp.setPopulationCategory(rs.getString("category"));
        rp.setTxnYear(rs.getDouble("txn_year"));
        rp.setRevenue(rs.getLong("revenue"));
        rpList.add(rp);
      }
      rs.close();
      return rpList;
    } catch (SQLException e) {
      return null;
    }
  }

  @Override
  public List<Integer> getRevenueYears() {
    String sql = "SELECT DISTINCT(EXTRACT(YEAR FROM date)) FROM sold;";
    List<Integer> years = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Integer yr = rs.getInt(1);
        years.add(yr);
      }
      rs.close();
      return years;
    } catch (SQLException e) {
      return null;
    }
  }

  @Override
  public List<Integer> getRevenueMonths() {
    String sql = "SELECT DISTINCT(EXTRACT(MONTH FROM date)) FROM sold;";
    List<Integer> months = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Integer mt = rs.getInt(1);
        months.add(mt);
      }
      rs.close();
      return months;
    } catch (SQLException e) {
      return null;
    }
  }

  @Override
  public List<String> getStates() {
    String sql = "SELECT DISTINCT(state) FROM city;";
    List<String> states = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        String state = rs.getString(1);
        states.add(state);
      }
      rs.close();
      return states;
    } catch (SQLException e) {
      return null;
    }
  }

  @Override
  public List<HighestVolumeCateogry> getHighestVolumeCategory(String year, String month) {
    String sql = "WITH tempTable(city_name, state_name, total)" 
                 + "AS (SELECT ct.name, c.state, SUM(s.quantity) AS total " 
                 + "FROM sold s JOIN product p ON p.productid=s.productid " 
                 + "JOIN product_store_xref x ON x.productid = p.productid " 
                 + "JOIN store st ON st.storeid = x.storeid " 
                 + "JOIN city c on c.storeid = st.storeid " 
                 + "JOIN product_category_xref pcx on pcx.productid = p.productid " 
                 + "JOIN category ct ON ct.name=pcx.category_name " 
                 + "WHERE TO_CHAR(s.date, 'MM') = ? AND TO_CHAR(s.date, 'YYYY') = ? " 
                 + "GROUP BY ct.name, c.state) " 
                 + "SELECT city_name, state_name, total FROM tempTable " 
                 + "WHERE (city_name, total) IN " 
                 + "(SELECT city_name, MAX(total) FROM tempTable GROUP BY city_name)";

    List<HighestVolumeCateogry> hvList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ps.setString(1, month);
      ps.setString(2, year);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        HighestVolumeCateogry hv = new HighestVolumeCateogry();
        hv.setCityName(rs.getString("city_name"));
        hv.setStateName(rs.getString("state_name"));
        hv.setTotal(rs.getLong("total"));
        hvList.add(hv);
      }
      rs.close();
      return hvList;
    } catch (SQLException e) {
      return null;
    }
  }

  @Override
  public List<GroundhogDayReport> getGroundhogDayReport() {
    String sql = "SELECT extract(year FROM s.date) AS sale_year, SUM(s.quantity) AS annual_sale, " 
                 + "round(cast(SUM(quantity) AS decimal(7,2))/.365, 2) AS sales_per_day, "
                 + "SUM(case when to_char(s.date, 'MMdd')='1101' then s.quantity else 0 end) AS " 
                 + "groundhog_day_sale "
                 + "FROM sold s "
                 + "JOIN product p ON s.productid=p.productid "
                 + "JOIN product_category_xref x ON p.productid=x.productid "
                 + "JOIN category c ON x.category_name=c.name "
                 + "WHERE c.name='office' "
                 + "GROUP BY sale_year "
                 + "ORDER BY sale_year ASC;";

    List<GroundhogDayReport> gdList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        GroundhogDayReport gd = new GroundhogDayReport();
        gd.setYear(rs.getLong("sale_year"));
        gd.setAnnualSale(rs.getLong("annual_sale"));
        gd.setDailyAverage(rs.getLong("sales_per_day"));
        gd.setGhdTotal(rs.getLong("groundhog_day_sale"));
        gdList.add(gd);
      }
      rs.close();
      return gdList;
    } catch (SQLException e) {
      return null;
    }
  }

  @Override
  public List<StoreRevenueByStateByYear> getStoreRevenueByStoreByYear(String stateName) {
    String sql = "SELECT s.storeid, s.address AS store_address, c.name AS city_name, (EXTRACT" 
                 + "(YEAR FROM d.date)) AS revenue_year, SUM(d.price) AS revenue "
                 + "FROM store s "
                 + "JOIN city c ON s.storeid=c.storeid "
                 + "JOIN product_store_xref x ON s.storeid=x.storeid "
                 + "JOIN product p ON x.productid=p.productid "
                 + "JOIN sold d ON p.productid=d.productid "
                 + "where c.state=? "
                 + "GROUP BY s.storeid, s.address, c.name, revenue_year "
                 + "ORDER BY revenue_year ASC, revenue DESC;";
    List<StoreRevenueByStateByYear> srList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ps.setString(1, stateName);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        StoreRevenueByStateByYear sr = new StoreRevenueByStateByYear();
        sr.setStoreId(rs.getInt("storeid"));
        sr.setAddress(rs.getString("store_address"));
        sr.setCity(rs.getString("city_name"));
        sr.setYear(rs.getLong("revenue_year"));
        sr.setRevenue(rs.getLong("revenue"));
        srList.add(sr);
      }
      rs.close();
      return srList;
    } catch (SQLException e) {
      return null;
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
      PreparedStatement ps = ppConnxn.prepareCall(sql);
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
      PreparedStatement ps = ppConnxn.prepareCall(sql);
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
    }
  }

  @Override
  public List<CityMembershipTrend> getCityMembershipTrend(String city, String state, long year) {
    String sql = "SELECT c.storeid, s.address, c.name, COUNT(m.mid) AS total_membership FROM city c JOIN store s ON s.storeid=c.storeid JOIN membership m ON m.signup_store=s.storeid WHERE c.name=? AND c.state=? AND EXTRACT(YEAR FROM m.signup_date)=? GROUP BY c.storeid, s.address, c.name;\n";

    List<CityMembershipTrend> cmList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
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
    }
  }
}
