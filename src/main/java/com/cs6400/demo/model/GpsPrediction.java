package com.cs6400.demo.model;

public class GpsPrediction {
  private int productId;
  private String name;
  private int retailPrice;
  private Long unitsSold;
  private Long unitsSoldOnDiscount;
  private Long unitsSoldAtRetailPrice;
  private Long actualRevenue;
  private Long predictedRevenue;
  private Long difference;

  public int getProductId() {
    return productId;
  }

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getRetailPrice() {
    return retailPrice;
  }

  public void setRetailPrice(int retailPrice) {
    this.retailPrice = retailPrice;
  }

  public Long getUnitsSold() {
    return unitsSold;
  }

  public void setUnitsSold(Long unitsSold) {
    this.unitsSold = unitsSold;
  }

  public Long getUnitsSoldOnDiscount() {
    return unitsSoldOnDiscount;
  }

  public void setUnitsSoldOnDiscount(Long unitsSoldOnDiscount) {
    this.unitsSoldOnDiscount = unitsSoldOnDiscount;
  }

  public Long getUnitsSoldAtRetailPrice() {
    return unitsSoldAtRetailPrice;
  }

  public void setUnitsSoldAtRetailPrice(Long unitsSoldAtRetailPrice) {
    this.unitsSoldAtRetailPrice = unitsSoldAtRetailPrice;
  }

  public Long getActualRevenue() {
    return actualRevenue;
  }

  public void setActualRevenue(Long actualRevenue) {
    this.actualRevenue = actualRevenue;
  }

  public Long getPredictedRevenue() {
    return predictedRevenue;
  }

  public void setPredictedRevenue(Long predictedRevenue) {
    this.predictedRevenue = predictedRevenue;
  }

  public Long getDifference() {
    return difference;
  }

  public void setDifference(Long difference) {
    this.difference = difference;
  }
}
