package com.cs6400.demo.dao;

import com.cs6400.demo.model.CategoryReport;
import com.cs6400.demo.model.ManufacturerDetail;
import com.cs6400.demo.model.ManufacturerProduct;
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
    public List<ManufacturerProduct> getManufacturerProduct() {
        String sql = "SELECT m.name, COUNT(p.name), ROUND(avg(p.price), 2) AS average_price, MAX " 
                     + "(p.price),  MIN(p.price) FROM product p JOIN manufacturer m ON " 
                     + "p.mfg_name=m.name GROUP BY m.name ORDER BY average_price DESC LIMIT 100;";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);
        List<ManufacturerProduct> result = new ArrayList<>();
        for(Map<String, Object> row:rows) {
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
    public List<CategoryReport> getCategoryReport() {
        String sql = "SELECT c.name category_name, COUNT(p.productid) COUNT_product, COUNT(DISTINCT" 
                     + " p.mfg_name) unique_mfg, ROUND(avg(p.price), 2) average_price FROM " 
                     + "product p JOIN product_category_xref x ON p.productid = x.productid JOIN " 
                     + "category c ON x.category_name = c.name GROUP BY c.name ORDER BY c.name ASC";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);
        List<CategoryReport> result = new ArrayList<>();
        for(Map<String, Object> row:rows) {
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
    public List<ManufacturerDetail> getManufacturerDetail(String name) {
        String sql = "SELECT p.productid, p.name product_name, string_agg(c.name, ', ') as " 
                     + "category, p.price FROM product p JOIN product_category_xref x ON " 
                     + "p.productid = x.productid JOIN category c ON c.name = x.category_name " 
                     + "JOIN manufacturer m ON m.name = p.mfg_name WHERE m.name = '" + name + "' " 
                     + "GROUP BY p.productid ORDER BY p.price DESC;";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);
        List<ManufacturerDetail> result = new ArrayList<>();
        for(Map<String, Object> row:rows) {
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
    public int getMfgDiscount(String name) throws SQLException {
        String sql = "SELECT MAX(p.max_discount) FROM manufacturer m, product p where m.name=p.mfg_name AND m.name=? group by m.name;";
        PreparedStatement ps = getConnection().prepareCall(sql);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int i = rs.getInt(1);
        return i;
    }
}
