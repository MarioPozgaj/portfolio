package com.assignment.portfolio.dto;

import java.time.LocalDate;

public class ListingDto {

  private String symbol;
  private String name;
  private String exchange;
  private String assetType;
  private LocalDate ipoData;
  private LocalDate delistingDate;
  private String status;
  private Boolean isSubscribed;

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getExchange() {
    return exchange;
  }

  public void setExchange(String exchange) {
    this.exchange = exchange;
  }

  public String getAssetType() {
    return assetType;
  }

  public void setAssetType(String assetType) {
    this.assetType = assetType;
  }

  public LocalDate getIpoData() {
    return ipoData;
  }

  public void setIpoData(LocalDate ipoData) {
    this.ipoData = ipoData;
  }

  public LocalDate getDelistingDate() {
    return delistingDate;
  }

  public void setDelistingDate(LocalDate delistingDate) {
    this.delistingDate = delistingDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Boolean getSubscribed() {
    return isSubscribed;
  }

  public void setSubscribed(Boolean subscribed) {
    isSubscribed = subscribed;
  }
}
