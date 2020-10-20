package com.cs6400.demo.controller;

import com.cs6400.demo.dao.ReportRepository;
import com.cs6400.demo.model.CategoryReport;
import com.cs6400.demo.model.ManufacturerDetail;
import com.cs6400.demo.model.ManufacturerProduct;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/")
public class ReportController {
    @Autowired
    ReportRepository repo;

    @GetMapping("/manufacturerProduct")
    public List<ManufacturerProduct> getStatistics() {
        return repo.getManufacturerProduct();
    }
    
    @GetMapping("/categoryReport")
    public List<CategoryReport> getCategoryReport() {
        return repo.getCategoryReport();
    }

    @GetMapping("/manufacturerDetail/{name}")
    public List<ManufacturerDetail> getManufacturerDetail(@PathVariable String name) {
        return repo.getManufacturerDetail(name);
    }
    
    @GetMapping("/mfgMaxDiscount/{name}")
    public int getMfgDiscount(@PathVariable String name) throws SQLException {
        return repo.getMfgDiscount(name);
    }
}
