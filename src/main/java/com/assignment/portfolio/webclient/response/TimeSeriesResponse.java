package com.assignment.portfolio.webclient.response;

import java.util.List;

public class TimeSeriesResponse {

  private MetaData metaData;
  private List<StockUnit> stockUnits;

  public MetaData getMetaData() {
    return metaData;
  }

  public void setMetaData(MetaData metaData) {
    this.metaData = metaData;
  }

  public List<StockUnit> getStocks() {
    return stockUnits;
  }

  public void setStocks(List<StockUnit> stockUnits) {
    this.stockUnits = stockUnits;
  }
}
