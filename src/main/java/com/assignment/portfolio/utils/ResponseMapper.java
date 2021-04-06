package com.assignment.portfolio.utils;

import static com.assignment.portfolio.utils.CsvUtils.stringToDate;

import com.assignment.portfolio.webclient.response.MetaData;
import com.assignment.portfolio.webclient.response.StockUnit;
import com.assignment.portfolio.webclient.response.TimeSeriesResponse;
import java.util.ArrayList;
import java.util.Map;

public interface ResponseMapper {

  static TimeSeriesResponse mapTimeSeriesResponse(Map response) {
    TimeSeriesResponse timeSeriesResponse = new TimeSeriesResponse();
    MetaData metaData = new MetaData();
    final var metaDataResponse = (Map) response.get("Meta Data");
    metaData.setInformation((String) metaDataResponse.get("1. Information"));
    metaData.setSymbol((String) metaDataResponse.get("2. Symbol"));
    metaData.setLastRefreshed(stringToDate((String) metaDataResponse.get("3. Last Refreshed")));
    metaData.setOutputSize((String) metaDataResponse.get("4. Output Size"));
    metaData.setTimeZone((String) metaDataResponse.get("5. Time Zone"));
    timeSeriesResponse.setMetaData(metaData);

    final var timeSeriesData = (Map<String, Object>) response.get("Time Series (Daily)");

    var stockUnits = new ArrayList<StockUnit>();

    if (!timeSeriesData.isEmpty()) {
      timeSeriesData.entrySet().forEach(entry -> {
        final var stockUnit = new StockUnit();
        final var entryMap = (Map) entry.getValue();
        stockUnit.setDate(stringToDate(entry.getKey()));
        stockUnit.setOpen(Double.parseDouble((String) entryMap.get("1. open")));
        stockUnit.setHigh(Double.parseDouble((String) entryMap.get("2. high")));
        stockUnit.setLow(Double.parseDouble((String) entryMap.get("3. low")));
        stockUnit.setClose(Double.parseDouble((String) entryMap.get("4. close")));
        stockUnit.setVolume(Long.parseLong((String) entryMap.get("5. volume")));

        stockUnits.add(stockUnit);
      });
    }

    timeSeriesResponse.setStocks(stockUnits);

    return timeSeriesResponse;
  }

}
