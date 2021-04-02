package com.assignment.portfolio.webclient;

import com.assignment.portfolio.dto.Listing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

public class AlphaVantageClient {

  @Value("${api.key}")
  private String apiKey;

  private static final String URL = "https://www.alphavantage.co/";
  private static final String LISTINGS_QUERY = "query?function=LISTING_STATUS&apikey=";
  private static final String COMMA_DELIMITER = ",";
  private static final String NYSE = "NYSE";

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final RestTemplate restTemplate = new RestTemplate();

  public void getAllListings() throws FileNotFoundException {
    File file = restTemplate.execute(URL + LISTINGS_QUERY + apiKey, HttpMethod.GET, null, clientHttpResponse -> {
      File ret = File.createTempFile("download", "tmp");
      StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
      return ret;
    });
    var listings = new ArrayList<Listing>();

    if(file == null) {
      //TODO: throw exception;
    }

    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        var listing = getRecordFromLine(scanner.nextLine());
        if(listing != null && StringUtils.equals(listing.getExchange(), NYSE))
          listings.add(listing);
      }
    }
    System.out.println("done");
  }

  private Listing getRecordFromLine(String line) {
    List<String> values = new ArrayList<>();
    try (Scanner rowScanner = new Scanner(line)) {
      rowScanner.useDelimiter(COMMA_DELIMITER);
      while (rowScanner.hasNext()) {
        values.add(rowScanner.next());
      }
    }
    if(StringUtils.equals(values.get(4), "ipoDate")) {
      return null;
    }
    return getListing(values);
  }

  private Listing getListing(List<String> values) {
    var listing = new Listing();
    listing.setSymbol(values.get(0));
    listing.setName(values.get(1));
    listing.setExchange(values.get(2));
    listing.setAssetType(values.get(3));
    listing.setIpoData(stringToDate(values.get(4)));
    listing.setDelistingDate(stringToDate(values.get(5)));
    listing.setStatus(values.get(6));
    return listing;
  }

  private LocalDate stringToDate(String stringDate) {
    if(StringUtils.isNotBlank(stringDate) && !StringUtils.equals(stringDate, "null")) {
      return LocalDate.parse(stringDate, FORMATTER);
    }
    return null;
  }
}
