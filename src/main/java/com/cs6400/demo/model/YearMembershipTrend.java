package com.cs6400.demo.model;

public class YearMembershipTrend {
  private String city;
  private String state;
  private Double signupYear;
  private Long total;

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

  public Double getSignupYear() {
    return signupYear;
  }

  public void setSignupYear(Double signupYear) {
    this.signupYear = signupYear;
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }
}
