package com.assignment.portfolio.webclient;

import static com.assignment.portfolio.webclient.AlphaVantageClient.BASE_URL;
import static com.assignment.portfolio.webclient.AlphaVantageClient.LISTINGS_QUERY;
import static com.assignment.portfolio.webclient.AlphaVantageClient.TIME_SERIES_QUERY;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class AlphaVantageClientTest {

  private static final String IBM = "IBM";

  @Value("${api.key}")
  private String apiKey;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private AlphaVantageClient alphaVantageClient;

  private MockRestServiceServer mockServer;

  @BeforeEach
  public void startServer() {
    mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void cvsToListingTest() throws IOException, URISyntaxException {
    var url = BASE_URL + format(LISTINGS_QUERY, apiKey);
    var jsonResponse = getFileAsString("/listing_status.csv");
    mockSetup(url, jsonResponse);

    var listings = alphaVantageClient.getAllListings();

    assertFalse(listings.isEmpty());
    assertTrue(listings.size() == 3692);
    listings.forEach(listing -> assertEquals("NYSE", listing.getExchange()));
    verifyAndResetMockServer();
  }

  @Test
  public void cvsToListingTestException() throws URISyntaxException {
    var url = BASE_URL + format(LISTINGS_QUERY, apiKey);

    mockServer.expect(ExpectedCount.once(),
        requestTo(new URI(url)))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.NOT_FOUND)
        );

    assertThrows(RuntimeException.class, () -> alphaVantageClient.getAllListings());
    verifyAndResetMockServer();
  }

  @Test
  public void getStockValuesTest() throws IOException, URISyntaxException {
    final var url = BASE_URL + format(TIME_SERIES_QUERY, IBM, apiKey);
    var jsonResponse = getFileAsString("/IBM_TIME_SERIES.json");
    mockSetup(url, jsonResponse);

    var ibmStock = alphaVantageClient.getStockValues(IBM);
    assertNotNull(ibmStock);
    assertEquals("Daily Prices (open, high, low, close) and Volumes", ibmStock.getMetaData().getInformation());
    assertEquals("IBM", ibmStock.getMetaData().getSymbol());
    assertEquals(LocalDate.of(2021, 4, 1), ibmStock.getMetaData().getLastRefreshed());
    assertEquals("Compact", ibmStock.getMetaData().getOutputSize());
    assertEquals("US/Eastern", ibmStock.getMetaData().getTimeZone());

    verifyAndResetMockServer();
  }

  public static String getFileAsString(final String filePath) throws IOException {
    var inputStream = AlphaVantageClientTest.class.getResourceAsStream(filePath);
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        resultStringBuilder.append(line).append("\n");
      }
    }
    return resultStringBuilder.toString();
  }

  public void mockSetup(final String url, final String jsonResponse)
      throws URISyntaxException {
    mockServer.expect(ExpectedCount.manyTimes(), requestTo(new URI(url)))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));
  }

  private void verifyAndResetMockServer() {
    mockServer.verify();
    mockServer.reset();
  }
}
