package com.cs6400.demo.model;

import java.math.BigDecimal;

public class ManufacturerProduct {
  private String name;
  private long count;
  private BigDecimal averagePrice;
  private int maxPrice;
  private int minPrice;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public BigDecimal getAveragePrice() {
    return averagePrice;
  }

  public void setAveragePrice(BigDecimal averagePrice) {
    this.averagePrice = averagePrice;
  }

  public int getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(int maxPrice) {
    this.maxPrice = maxPrice;
  }

  public int getMinPrice() {
    return minPrice;
  }

  public void setMinPrice(int minPrice) {
    this.minPrice = minPrice;
  }
}
