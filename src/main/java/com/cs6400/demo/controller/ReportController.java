package com.cs6400.demo.controller;

import com.cs6400.demo.dao.ReportRepository;
import com.cs6400.demo.model.CategoryReport;
import com.cs6400.demo.model.CityMembershipTrend;
import com.cs6400.demo.model.YearMembershipTrend;
import com.cs6400.demo.model.ManufacturerDetail;
import com.cs6400.demo.model.ManufacturerProduct;
import com.cs6400.demo.model.MembershipTrend;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/")
public class ReportController {
  @Autowired
  ReportRepository repo;

  @GetMapping("/manufacturerProduct")
  public List<ManufacturerProduct> getStatistics() throws SQLException {
    return repo.getManufacturerProduct();
  }

  @GetMapping("/categoryReport")
  public List<CategoryReport> getCategoryReport() throws SQLException {
    return repo.getCategoryReport();
  }

  @GetMapping("/manufacturerDetail/{name}")
  public List<ManufacturerDetail> getManufacturerDetail(@PathVariable String name) throws SQLException {
    return repo.getManufacturerDetail(name);
  }

  @GetMapping("/mfgMaxDiscount/{name}")
  public int getMfgDiscount(@PathVariable String name) throws SQLException {
    return repo.getMfgDiscount(name);
  }

  @GetMapping("/membershipTrend")
  public List<MembershipTrend> getMembershipTrend() {
    return repo.getMembershipTrend();
  }

  @GetMapping("/yearMembershipTrend")
  public List<YearMembershipTrend> getYearMembershipTrend(@RequestParam long year) {
    return repo.getYearMembershipTrend(year);
  }

  @GetMapping("/cityMembershipTrend")
  public List<CityMembershipTrend> getCityMembershipTrend(@RequestParam String city,
                                                          @RequestParam String state,
                                                          @RequestParam long year) {
    return repo.getCityMembershipTrend(city, state, year);
  }
}
