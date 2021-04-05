package com.assignment.portfolio.webclient.response;

import java.time.LocalDate;

public class MetaData {

  private String information;
  private String symbol;
  private LocalDate lastRefreshed;
  private String timeZone;
  private String interval;
  private String outputSize;

  public String getInformation() {
    return information;
  }

  public void setInformation(String information) {
    this.information = information;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public LocalDate getLastRefreshed() {
    return lastRefreshed;
  }

  public void setLastRefreshed(LocalDate lastRefreshed) {
    this.lastRefreshed = lastRefreshed;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public String getInterval() {
    return interval;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }

  public String getOutputSize() {
    return outputSize;
  }

  public void setOutputSize(String outputSize) {
    this.outputSize = outputSize;
  }
}
