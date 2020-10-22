package com.cs6400.demo.model;

public class RevenuePopulation {
  private String populationCategory;
  private Double txnYear;
  private Long revenue;

  public String getPopulationCategory() {
    return populationCategory;
  }

  public void setPopulationCategory(String populationCategory) {
    this.populationCategory = populationCategory;
  }

  public Double getTxnYear() {
    return txnYear;
  }

  public void setTxnYear(Double txnYear) {
    this.txnYear = txnYear;
  }

  public Long getRevenue() {
    return revenue;
  }

  public void setRevenue(Long revenue) {
    this.revenue = revenue;
  }
}
