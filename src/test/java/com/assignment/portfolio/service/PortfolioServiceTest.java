package com.assignment.portfolio.service;

import static com.assignment.portfolio.webclient.AlphaVantageClient.BASE_URL;
import static com.assignment.portfolio.webclient.AlphaVantageClient.LISTINGS_QUERY;
import static com.assignment.portfolio.webclient.AlphaVantageClient.TIME_SERIES_QUERY;
import static com.assignment.portfolio.webclient.AlphaVantageClientTest.getFileAsString;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.assignment.portfolio.service.impl.PortfolioServiceImpl;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class PortfolioServiceTest {

  private static final String USER = "user";
  private static final String USER_2 = "user2";
  private static final String IBM = "IBM";
  private static final String AMD = "AMD";
  private static final String AA = "AA";

  @Value("${api.key}")
  private String apiKey;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private PortfolioServiceImpl portfolioService;

  private MockRestServiceServer mockServer;

  private static int setupCounter = 0;

  @BeforeEach
  public void startServer() throws IOException, URISyntaxException {
    mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();//.createServer(restTemplate);

    if(setupCounter == 0) {
      setupCounter++;
      var url = BASE_URL + format(LISTINGS_QUERY, apiKey);
      var jsonResponse = getFileAsString("/listing_status.csv");
      mockSetup(url, jsonResponse);
      portfolioService.getListings();
      verifyAndResetMockServer();
    }
  }

  @Test
  public void findListingsTest() throws Exception {
    assertTrue(portfolioService.subscribeToPortfolio(IBM, USER));
    verifyAndResetMockServer();

    var url = BASE_URL + format(TIME_SERIES_QUERY, IBM,  apiKey);
    var jsonResponse = getFileAsString("/IBM_TIME_SERIES.json");
    mockSetup(url, jsonResponse);

    portfolioService.getUserPortfolio(USER);
    verifyAndResetMockServer();
  }


  @Test
  public void subscribeTest() throws Exception {
    assertFalse(portfolioService.subscribeToPortfolio(AMD, USER));
    assertTrue(portfolioService.subscribeToPortfolio(AA, USER));
  }

  @Test
  public void unsubscribeTest() throws Exception {
    assertTrue(portfolioService.subscribeToPortfolio(IBM, USER));
    assertTrue(portfolioService.unsubscribeToPortfolio(IBM, USER));
    assertFalse(portfolioService.unsubscribeToPortfolio(IBM, USER));
    verifyAndResetMockServer();
  }

  @Test
  public void subscriptionTest() throws IOException, URISyntaxException {
    assertTrue(portfolioService.subscribeToPortfolio(IBM, USER));
    assertTrue(portfolioService.subscribeToPortfolio(AA, USER));
    assertTrue(portfolioService.subscribeToPortfolio(IBM, USER_2));

    final var ibmUrl = BASE_URL + format(TIME_SERIES_QUERY, IBM, apiKey);
    var ibmJsonResponse = getFileAsString("/IBM_TIME_SERIES.json");
    mockSetup(ibmUrl, ibmJsonResponse);
    final var aaUrl = BASE_URL + format(TIME_SERIES_QUERY, AA, apiKey);
    var aaJsonResponse = getFileAsString("/AA_TIME_SERIES.json");
    mockSetup(aaUrl, aaJsonResponse);

    final var portfolio = portfolioService.getUserPortfolio(USER);
    assertFalse(portfolio.isEmpty());
    assertEquals(2, portfolio.size());
    verifyAndResetMockServer();

    mockSetup(ibmUrl, ibmJsonResponse);
    final var portfolio2 = portfolioService.getUserPortfolio(USER_2);
    assertFalse(portfolio2.isEmpty());
    assertEquals(1, portfolio2.size());
    verifyAndResetMockServer();
    assertTrue(portfolioService.unsubscribeToPortfolio(IBM, USER));
    assertTrue(portfolioService.unsubscribeToPortfolio(AA, USER));
    assertTrue(portfolioService.unsubscribeToPortfolio(IBM, USER_2));
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
