package com.cs6400.demo.model;

public class StoreRevenueByStateByYear {
  private int storeId;
  private String address;
  private String city;
  private Long year;
  private Long revenue;

  public int getStoreId() {
    return storeId;
  }

  public void setStoreId(int storeId) {
    this.storeId = storeId;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Long getYear() {
    return year;
  }

  public void setYear(Long year) {
    this.year = year;
  }

  public Long getRevenue() {
    return revenue;
  }

  public void setRevenue(Long revenue) {
    this.revenue = revenue;
  }
}
