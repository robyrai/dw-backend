package com.cs6400.demo.model;

public class MembershipTrend {
  private String city;
  private String state;
  private long year;
  private long total;
  private boolean isMultiple;

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public long getYear() {
    return year;
  }

  public void setYear(long year) {
    this.year = year;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public boolean isMultiple() {
    return isMultiple;
  }

  public void setMultiple(boolean multiple) {
    isMultiple = multiple;
  }
}
