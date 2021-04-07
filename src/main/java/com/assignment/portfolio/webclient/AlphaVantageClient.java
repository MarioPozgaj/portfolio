package com.assignment.portfolio.webclient;

import static com.assignment.portfolio.utils.CsvUtils.getListingsFromCsv;
import static com.assignment.portfolio.utils.ResponseMapper.mapTimeSeriesResponse;
import static java.lang.String.format;

import com.assignment.portfolio.dto.ListingDto;
import com.assignment.portfolio.exception.RestClientException;
import com.assignment.portfolio.webclient.response.TimeSeriesResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class AlphaVantageClient {

  @Value("${api.key}")
  private String apiKey;

  public static final String BASE_URL = "https://www.alphavantage.co/";
  public static final String LISTINGS_QUERY = "query?function=LISTING_STATUS&apikey=%s";
  public static final String TIME_SERIES_QUERY = "query?function=TIME_SERIES_DAILY&symbol=%1$s&apikey=%2$s";

  private RestTemplate restTemplate;

  public AlphaVantageClient(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<ListingDto> getAllListings() {
    File file = restTemplate
        .execute(BASE_URL + format(LISTINGS_QUERY, apiKey), HttpMethod.GET, null, clientHttpResponse -> {
          File ret = File.createTempFile("download", "tmp");
          StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
          return ret;
        });

    if(file == null) {
      throw new RuntimeException("Failed to get listings");
    }

    return getListingsFromCsv(file);
  }

  public TimeSeriesResponse getStockValues(String symbol) {
    var response = restTemplate
        .getForEntity(BASE_URL + format(TIME_SERIES_QUERY, symbol, apiKey), Map.class);

    if (!HttpStatus.OK.equals(response.getStatusCode())) {
      throw new RestClientException(format("Failed to get time series for %s", symbol));
    }
    var map = response.getBody();

    return mapTimeSeriesResponse(map);
  }
}
