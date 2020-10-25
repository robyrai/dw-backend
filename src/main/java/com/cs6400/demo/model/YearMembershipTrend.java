package com.cs6400.demo.model;

public class YearMembershipTrend {
  private String city;
  private String state;
  private Long total;
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

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public boolean isMultiple() {
    return isMultiple;
  }

  public void setMultiple(boolean multiple) {
    isMultiple = multiple;
  }
}
