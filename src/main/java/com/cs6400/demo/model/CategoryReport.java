package com.cs6400.demo.model;

import java.math.BigDecimal;

public class CategoryReport {
  private String categoryName;
  private long countProduct;
  private long uniqueMfg;
  private BigDecimal avgPrice;

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public long getCountProduct() {
    return countProduct;
  }

  public void setCountProduct(long countProduct) {
    this.countProduct = countProduct;
  }

  public long getUniqueMfg() {
    return uniqueMfg;
  }

  public void setUniqueMfg(long uniqueMfg) {
    this.uniqueMfg = uniqueMfg;
  }

  public BigDecimal getAvgPrice() {
    return avgPrice;
  }

  public void setAvgPrice(BigDecimal avgPrice) {
    this.avgPrice = avgPrice;
  }
}
