package com.cs6400.demo.dao;

import com.cs6400.demo.model.CategoryReport;
import com.cs6400.demo.model.CityMembershipTrend;
import com.cs6400.demo.model.GpsPrediction;
import com.cs6400.demo.model.GroundhogDayReport;
import com.cs6400.demo.model.HighestVolumeCateogry;
import com.cs6400.demo.model.RevenuePopulation;
import com.cs6400.demo.model.StoreRevenueByStateByYear;
import com.cs6400.demo.model.YearMembershipTrend;
import com.cs6400.demo.model.ManufacturerDetail;
import com.cs6400.demo.model.ManufacturerProduct;
import com.cs6400.demo.model.MembershipTrend;
import java.sql.SQLException;
import java.util.List;

public interface ReportRepository {
  List<ManufacturerProduct> getManufacturerProduct() throws SQLException;

  List<CategoryReport> getCategoryReport() throws SQLException;

  List<ManufacturerDetail> getManufacturerDetail(String name) throws SQLException;

  int getMfgDiscount(String name) throws SQLException;
  
  List<GpsPrediction> getGpsPrediction();

  List<MembershipTrend> getMembershipTrend();

  List<CityMembershipTrend> getCityMembershipTrend(String city, String state, long year);

  List<RevenuePopulation> getRevenueByPopulation();

  List<Integer> getRevenueYears();

  List<Integer> getRevenueMonths();

  List<String> getStates();

  List<HighestVolumeCateogry> getHighestVolumeCategory(String year, String month);

  List<GroundhogDayReport> getGroundhogDayReport();

  List<YearMembershipTrend> getYearCityMembershipTrendTop(long year);

  List<YearMembershipTrend> getYearCityMembershipTrendBottom(long year);

  List<StoreRevenueByStateByYear> getStoreRevenueByStoreByYear(String stateName);
}
