package com.assignment.portfolio.webclient;

import static com.assignment.portfolio.utils.CsvUtils.getListingsFromCsv;

import com.assignment.portfolio.dto.Listing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class AlphaVantageClient {

  @Value("${api.key}")
  private String apiKey;

  protected static final String BASE_URL = "https://www.alphavantage.co/";
  protected static final String LISTINGS_QUERY = "query?function=LISTING_STATUS&apikey=";

  private RestTemplate restTemplate;

  AlphaVantageClient(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Listing> getAllListings() throws FileNotFoundException {
    File file = restTemplate
        .execute(BASE_URL + LISTINGS_QUERY + apiKey, HttpMethod.GET, null, clientHttpResponse -> {
          File ret = File.createTempFile("download", "tmp");
          StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
          return ret;
        });

    if(file == null) {
      throw new RuntimeException("Failed to get listings");
    }

    return getListingsFromCsv(file);
  }


}
