package com.cs6400.demo.dao;

import com.cs6400.demo.model.CategoryReport;
import com.cs6400.demo.model.ManufacturerDetail;
import com.cs6400.demo.model.ManufacturerProduct;

import java.sql.SQLException;
import java.util.List;

public interface ReportRepository {
    List<ManufacturerProduct> getManufacturerProduct();

    List<CategoryReport> getCategoryReport();
    
    List<ManufacturerDetail> getManufacturerDetail(String name);

    int getMfgDiscount(String name) throws SQLException;
}
