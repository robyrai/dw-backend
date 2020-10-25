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

  // Manufacturer's Product Report
  @Override
  public List<ManufacturerProduct> getManufacturerProduct() throws SQLException {
    String sql = "SELECT m.name, COUNT(p.name), ROUND(avg(p.price), 2) AS average_price, "
                 + "MAX(p.price),  MIN(p.price) "
                 + "FROM product p JOIN manufacturer m ON p.manufacturer = m.name "
                 + "GROUP BY m.name "
                 + "ORDER BY average_price DESC "
                 + "LIMIT 100;";
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

  // Drill-down manufacturer first part to get the max discount the manufacturer offers
  @Override
  public int getMfgDiscount(String name) {
    String sql = "SELECT MAX(m.MaxDiscount) FROM manufacturer m, product p "
                 + "WHERE m.name = p.manufacturer AND m.name=?";
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

  // Drill-down manufacturer Report
  @Override
  public List<ManufacturerDetail> getManufacturerDetail(String name) {
    String sql = "SELECT p.PID, p.name product_name, string_agg(c.name, ', ') as category ,p.price "
                 + "FROM product p, category c, ProductCategory x "
                 + "WHERE p.PID = x.PID AND c.name = x.category AND p.manufacturer = '" + name + "' "
                 + "GROUP BY p.PID "
                 + "ORDER BY p.price DESC;";
    List<Map<String, Object>> rows = ppJdbcTemplate.queryForList(sql);
    List<ManufacturerDetail> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      ManufacturerDetail md = new ManufacturerDetail();
      md.setProductId((Integer) row.get("pid"));
      md.setName((String) row.get("product_name"));
      md.setCategory((String) row.get("category"));
      md.setPrice((Integer) row.get("price"));
      result.add(md);
    }
    return result;
  }

  // Category Report
  @Override
  public List<CategoryReport> getCategoryReport() {
    String sql = "SELECT c.name, COUNT(p.PID) product_count, COUNT(DISTINCT p.Name) "
                 + "manufacturer_count, ROUND(avg(p.price), 2) average_price "
                 + "FROM product p "
                 + "JOIN ProductCategory x ON p.PID = x.PID "
                 + "JOIN category c ON x.category = c.name "
                 + "GROUP BY c.name "
                 + "ORDER BY c.name ASC;";
    List<Map<String, Object>> rows = ppJdbcTemplate.queryForList(sql);
    List<CategoryReport> result = new ArrayList<>();
    for (Map<String, Object> row : rows) {
      CategoryReport cr = new CategoryReport();
      cr.setCategoryName((String) row.get("name"));
      cr.setCountProduct((Long) row.get("product_count"));
      cr.setUniqueMfg((Long) row.get("manufacturer_count"));
      cr.setAvgPrice((BigDecimal) row.get("average_price"));
      result.add(cr);
    }
    return result;
  }

  // Actual vs predicted revenue for GPS units report
  @Override
  public List<GpsPrediction> getGpsPrediction() {
    String sql = "SELECT PID, name, retail_price, total_ever_sold, total_sold_at_discount, "
                 + "total_sold_at_retail, actual_revenue, predicted_revenue, difference "
                 + "FROM ("
                 + "SELECT PID, Name, retail_price, total_ever_sold, total_sold_at_discount, "
                 + "total_sold_at_retail, actual_revenue, predicted_revenue, "
                 + "(actual_revenue - predicted_revenue) AS difference "
                 + "FROM ("
                 + "SELECT p.PID, p.name, p.price retail_price, "
                 + "(SELECT SUM(SoldWithPrice.Quantity)"
                 + "FROM SoldWithPrice "
                 + "WHERE SoldWithPrice.PID = p.pID) AS total_ever_sold, "
                 + "(SELECT SUM(SoldWIthPrice.Quantity)"
                 + "FROM SoldWithPrice "
                 + "WHERE SoldWithPrice.PID = p.PID AND SoldWithPrice.Sale = true) "
                 + "total_sold_at_discount, "
                 + " (SELECT SUM(SoldWIthPrice.Quantity) FROM SoldWithPrice "
                 + "WHERE SoldWithPrice.PID = p.PID AND SoldWithPrice.Sale = false) "
                 + "total_sold_at_retail, "
                 + "(SELECT SUM(SoldWithPrice.Price)"
                 + "FROM SoldWithPrice "
                 + "WHERE SoldWithPrice.PID = p.PID "
                 + "GROUP BY SoldWithPrice.PID) AS actual_revenue, "
                 + "(((SELECT SUM(SoldWithPrice.Quantity) "
                 + "FROM SoldWithPrice "
                 + "WHERE SoldWithPrice.PID = p.pID) * 0.75) * "
                 + "(SELECT productSub.Price "
                 + "FROM Product AS productSub "
                 + "WHERE productSub.PID = p.pID)) AS predicted_revenue "
                 + "FROM product p "
                 + "JOIN soldWithPrice d ON d.PID = p.PID "
                 + "LEFT JOIN sale s ON d.PID = s.PID AND d.date = s.date "
                 + "JOIN ProductCategory x ON p.PID = x.PID "
                 + "JOIN category c ON x.category = c.name "
                 + "WHERE c.name = 'GPS'"
                 + "GROUP BY p.PID, p.Name, p.price) AS subQuery ) AS secondSubQuery "
                 + "WHERE ABS(difference) > 5000 "
                 + "ORDER BY difference DESC;";
    List<GpsPrediction> gpList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        GpsPrediction gp = new GpsPrediction();
        gp.setProductId(rs.getInt("pid"));
        gp.setName(rs.getString("name"));
        gp.setRetailPrice(rs.getInt("retail_price"));
        gp.setUnitsSold(rs.getLong("total_ever_sold"));
        gp.setUnitsSoldOnDiscount(rs.getLong("total_sold_at_discount"));
        gp.setUnitsSoldAtRetailPrice(rs.getLong("total_sold_at_retail"));
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

  // Store revenue by year by state report
  // First get all states names to list in dropdown option
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

  // Store revenue by year by state report
  @Override
  public List<StoreRevenueByStateByYear> getStoreRevenueByStoreByYear(String stateName) {
    String sql = "SELECT s.storeid, s.address store_address, s.City city_name, "
                 + "(EXTRACT(YEAR FROM d.date)) revenue_year, SUM(d.price) revenue "
                 + "FROM store s "
                 + "JOIN SoldWithPrice d ON s.StoreId=d.StoreId "
                 + "WHERE s.state=? "
                 + "GROUP BY s.storeid, s.address, s.City, revenue_year "
                 + "ORDER BY revenue_year ASC, revenue DESC";
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

  // Ground-hog day report
  @Override
  public List<GroundhogDayReport> getGroundhogDayReport() {
    String sql = "SELECT extract(year FROM s.date) year, SUM(s.quantity) total_num_sales, "
                 + "round(cast(SUM(s.quantity) decimal(7,2))/.365, 2) sales_per_day, "
                 + "SUM(CASE WHEN to_char(s.date, 'MMdd')='0202' THEN s.quantity ELSE 0 END) "
                 + "groundhog_day_sale "
                 + "FROM sold s "
                 + "JOIN product p ON s.PID=p.PID "
                 + "JOIN ProductCategory x ON p.PID=x.PID "
                 + "JOIN category c ON x.category=c.name "
                 + "WHERE c.name='Air Conditioner' "
                 + "GROUP BY year "
                 + "ORDER BY year ASC";

    List<GroundhogDayReport> gdList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        GroundhogDayReport gd = new GroundhogDayReport();
        gd.setYear(rs.getLong("year"));
        gd.setAnnualSale(rs.getLong("total_number_sales"));
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

  // State With Highest volume for each category report
  // First select year and month
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

  // First select year and month
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

  // State With Highest volume for each category report
  @Override
  public List<HighestVolumeCateogry> getHighestVolumeCategory(String year, String month) {
    String sql = "WITH tempTable(category, state, total) (SELECT ct.name, st.state, SUM(s.quantity) total "
                 + "FROM sold s "
                 + "JOIN product p ON p.PID=s.PID "
                 + "JOIN store st ON st.storeid = s.storeid "
                 + "JOIN ProductCategory pcx on pcx.PID = p.PID "
                 + "JOIN category ct ON ct.name=pcx.category "
                 + "WHERE TO_CHAR(s.date, 'MM') = ? AND TO_CHAR(s.date, 'YYYY') = ? "
                 + "GROUP BY ct.name, st.state) "
                 + "SELECT category, state, total FROM tempTable "
                 + "WHERE (category, total) IN "
                 + "(SELECT category, MAX(total) "
                 + "FROM tempTable "
                 + "GROUP BY category) "
                 + "ORDER BY category ASC";

    List<HighestVolumeCateogry> hvList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ps.setString(1, month);
      ps.setString(2, year);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        HighestVolumeCateogry hv = new HighestVolumeCateogry();
        hv.setCategory(rs.getString("category"));
        hv.setStateName(rs.getString("state"));
        hv.setTotal(rs.getLong("total"));
        hvList.add(hv);
      }
      rs.close();
      return hvList;
    } catch (SQLException e) {
      return null;
    }
  }

  // Revenue by population report
  @Override
  public List<RevenuePopulation> getRevenueByPopulation() {
    String sql = "SELECT t.category, t.year, t.revenue FROM (SELECT "
                 + "  CASE  "
                 + "    WHEN c.population > 9000000 THEN 'Extra Large' "
                 + "    WHEN c.population > 6700000 THEN 'Large' "
                 + "    WHEN c.population > 3700000 THEN 'Medium' "
                 + "    ELSE 'Small' "
                 + "  END as category, "
                 + "  CASE "
                 + "    WHEN c.population > 9000000 THEN 3 "
                 + "  WHEN c.population > 6700000 THEN 2 "
                 + "  WHEN c.population > 3700000 THEN 1 "
                 + "  ELSE 0 "
                 + "  END as rnk, "
                 + "  EXTRACT(YEAR FROM d.date) AS year, "
                 + "  round(SUM(d.price)/COUNT(DISTINCT(c.name)), 2) AS revenue "
                 + "FROM soldWithPrice d "
                 + "JOIN store s ON s.storeid=d.storeid "
                 + "JOIN city c ON c.name=s.city "
                 + "GROUP BY category, year, rnk "
                 + "ORDER BY year ASC, rnk ASC) as t";
    List<RevenuePopulation> rpList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        RevenuePopulation rp = new RevenuePopulation();
        rp.setPopulationCategory(rs.getString("category"));
        rp.setTxnYear(rs.getDouble("year"));
        rp.setRevenue(rs.getLong("revenue"));
        rpList.add(rp);
      }
      rs.close();
      return rpList;
    } catch (SQLException e) {
      return null;
    }
  }

  // Membership trend report part 1 - yearly report
  @Override
  public List<MembershipTrend> getMembershipTrend() {
    String sql = "SELECT EXTRACT(YEAR FROM m.date) AS signup_year, COUNT(m.date) AS total "
                 + "FROM membership m "
                 + "JOIN store s ON s.storeid= m.storeId "
                 + "GROUP BY signup_year "
                 + "ORDER BY signup_year DESC";
    List<MembershipTrend> mtList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        MembershipTrend mt = new MembershipTrend();
        mt.setYear(rs.getLong("signup_year"));
        mt.setTotal(rs.getLong("total"));
        mtList.add(mt);
      }
      rs.close();
      return mtList;
    } catch (SQLException e) {
      return null;
    }
  }

  // Membership trend report part 2 - city wise report top 25
  @Override
  public List<YearMembershipTrend> getYearCityMembershipTrendTop(long year) {
    String sql = "SELECT t.city, t.state, t.total, CASE WHEN u.count > 1 THEN true ELSE false END"
                 + " AS isMultiple "
                 + "FROM (SELECT s.city, s.state, COUNT(m.date) AS total "
                 + "  FROM membership m "
                 + "  JOIN store s ON s.storeid=m.storeId "
                 + "  WHERE EXTRACT(YEAR FROM m.date)=? "
                 + "  GROUP BY city, state "
                 + "  ORDER BY total DESC "
                 + "  LIMIT 25) t "
                 + "JOIN (select city, state, count(storeid) from store group by city, state) u "
                 + "ON t.city=u.city AND t.state = u.state;";

    List<YearMembershipTrend> ymList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ps.setLong(1, year);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        YearMembershipTrend ym = new YearMembershipTrend();
        ym.setCity(rs.getString("city"));
        ym.setState(rs.getString("state"));
        ym.setTotal(rs.getLong("total"));
        ym.setMultiple(rs.getBoolean("isMultiple"));
        ymList.add(ym);
      }
      rs.close();
      return ymList;
    } catch (SQLException e) {
      return null;
    }
  }

  // Membership trend report part 2 - city wise report bottom 25
  @Override
  public List<YearMembershipTrend> getYearCityMembershipTrendBottom(long year) {
    String sql = "SELECT t.city, t.state, t.total, CASE WHEN u.count > 1 THEN true ELSE false END"
                 + " AS isMultiple "
                 + "FROM (SELECT s.city, s.state, COUNT(m.date) AS total "
                 + "  FROM membership m "
                 + "  JOIN store s ON s.storeid=m.storeId "
                 + "  WHERE EXTRACT(YEAR FROM m.date)=? "
                 + "  GROUP BY city, state "
                 + "  ORDER BY total ASC "
                 + "  LIMIT 25) t "
                 + "JOIN (select city, state, count(storeid) from store group by city, state) u "
                 + "ON t.city=u.city AND t.state = u.state;";

    List<YearMembershipTrend> ymList = new ArrayList<>();
    try {
      PreparedStatement ps = ppConnxn.prepareCall(sql);
      ps.setLong(1, year);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        YearMembershipTrend ym = new YearMembershipTrend();
        ym.setCity(rs.getString("city"));
        ym.setState(rs.getString("state"));
        ym.setTotal(rs.getLong("total"));
        ym.setMultiple(rs.getBoolean("isMultiple"));
        ymList.add(ym);
      }
      rs.close();
      return ymList;
    } catch (SQLException e) {
      return null;
    }
  }

  // Membership trend report part 3 - store wise report
  @Override
  public List<CityMembershipTrend> getCityMembershipTrend(String city, String state, long year) {
    String sql = "SELECT s.storeid, s.address, s.city, COUNT(m.membershipId) AS total_membership "
                 + "FROM store s "
                 + "JOIN membership m ON m.storeId=s.storeid "
                 + "WHERE s.city=? AND s.state=? AND EXTRACT(YEAR FROM m.date)=? "
                 + "GROUP BY s.storeid, s.address, s.city;";

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
        cm.setCity(rs.getString("city"));
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
