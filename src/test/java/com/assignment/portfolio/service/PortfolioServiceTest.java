package com.assignment.portfolio.service;

import static com.assignment.portfolio.webclient.AlphaVantageClient.BASE_URL;
import static com.assignment.portfolio.webclient.AlphaVantageClient.LISTINGS_QUERY;
import static com.assignment.portfolio.webclient.AlphaVantageClient.TIME_SERIES_QUERY;
import static com.assignment.portfolio.webclient.AlphaVantageClientTest.getFileAsString;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.assignment.portfolio.dto.FilterAndSortingDto;
import com.assignment.portfolio.dto.FilterAndSortingDto.Direction;
import com.assignment.portfolio.dto.PaginationDto;
import com.assignment.portfolio.dto.SearchCriteriaDto;
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
  public void setup() throws IOException, URISyntaxException {
    mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();

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
    assertTrue(portfolioService.unsubscribeToPortfolio(IBM, USER));
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

  @Test
  public void findListingsDefaultTest() {
    var searchCriteria = new SearchCriteriaDto();
    searchCriteria.setPaginationDto(new PaginationDto());
    searchCriteria.setFilterAndSortingDto(new FilterAndSortingDto());
    var findListingsResultDto = portfolioService.findListings(searchCriteria, USER);
    assertNotNull(findListingsResultDto);
    var listings = findListingsResultDto.getListings();
    assertEquals(3692, findListingsResultDto.getTotalItems());
    assertNotNull(listings);
    assertFalse(listings.isEmpty());
    assertEquals(20, listings.size());
    assertEquals("A", listings.get(0).getSymbol());
  }

  @Test
  public void findListingsLast10Test() {
    var searchCriteria = new SearchCriteriaDto();
    var pagination = new PaginationDto();
    pagination.setSize(10);
    searchCriteria.setPaginationDto(pagination);
    var filterAndSorting = new FilterAndSortingDto();
    filterAndSorting.setSortingField("symbol");
    filterAndSorting.setSortDirection(Direction.DESC);
    searchCriteria.setFilterAndSortingDto(filterAndSorting);
    var findListingsResultDto = portfolioService.findListings(searchCriteria, USER);
    assertNotNull(findListingsResultDto);
    var listings = findListingsResultDto.getListings();
    assertEquals(3692, findListingsResultDto.getTotalItems());
    assertNotNull(listings);
    assertFalse(listings.isEmpty());
    assertEquals(10, listings.size());
    assertEquals("ZYME", listings.get(0).getSymbol());
  }

  @Test
  public void findListingsDefaultWithSubscriptionTest() throws Exception {
    var searchCriteria = new SearchCriteriaDto();
    searchCriteria.setPaginationDto(new PaginationDto());
    searchCriteria.setFilterAndSortingDto(new FilterAndSortingDto());
    var url = BASE_URL + format(TIME_SERIES_QUERY, IBM,  apiKey);
    var jsonResponse = getFileAsString("/AA_TIME_SERIES.json");
    mockSetup(url, jsonResponse);
    assertTrue(portfolioService.subscribeToPortfolio(AA, USER));
    var findListingsResultDto = portfolioService.findListings(searchCriteria, USER);
    assertNotNull(findListingsResultDto);
    var listings = findListingsResultDto.getListings();
    assertEquals(3692, findListingsResultDto.getTotalItems());
    assertNotNull(listings);
    assertFalse(listings.isEmpty());
    assertEquals(20, listings.size());
    assertEquals("A", listings.get(0).getSymbol());
    assertFalse(listings.get(0).getSubscribed());
    assertTrue(listings.get(1).getSubscribed());
  }

  @Test
  public void filterListingsBySymbolTest() {
    var searchCriteria = new SearchCriteriaDto();
    searchCriteria.setPaginationDto(new PaginationDto());
    var filterAndSortingDto = new FilterAndSortingDto();
    filterAndSortingDto.setFilterValue("AAC");
    searchCriteria.setFilterAndSortingDto(filterAndSortingDto);
    var findListingsResultDto = portfolioService.findListings(searchCriteria, USER);
    assertNotNull(findListingsResultDto);
    var listings = findListingsResultDto.getListings();
    assertEquals(3, findListingsResultDto.getTotalItems());
    assertNotNull(listings);
    assertFalse(listings.isEmpty());
    assertEquals(3, listings.size());
    assertEquals("AAC", listings.get(0).getSymbol());
    assertEquals("AAC-U", listings.get(1).getSymbol());
    assertEquals("AAC-WS", listings.get(2).getSymbol());
  }

  @Test
  public void filterListingsBySymbolDescending() {
    var searchCriteria = new SearchCriteriaDto();
    searchCriteria.setPaginationDto(new PaginationDto());
    var filterAndSortingDto = new FilterAndSortingDto();
    filterAndSortingDto.setFilterValue("AAC");
    filterAndSortingDto.setSortDirection(Direction.DESC);
    searchCriteria.setFilterAndSortingDto(filterAndSortingDto);
    var findListingsResultDto = portfolioService.findListings(searchCriteria, USER);
    assertNotNull(findListingsResultDto);
    var listings = findListingsResultDto.getListings();
    assertEquals(3, findListingsResultDto.getTotalItems());
    assertNotNull(listings);
    assertFalse(listings.isEmpty());
    assertEquals(3, listings.size());
    assertEquals("AAC-WS", listings.get(0).getSymbol());
    assertEquals("AAC-U", listings.get(1).getSymbol());
    assertEquals("AAC", listings.get(2).getSymbol());
  }

  @Test
  public void filterListingsByNameTest() {
    var searchCriteria = new SearchCriteriaDto();
    searchCriteria.setPaginationDto(new PaginationDto());
    var filterAndSortingDto = new FilterAndSortingDto();
    filterAndSortingDto.setFilterField("name");
    filterAndSortingDto.setFilterValue("Ares Acquisition Corporation - Class A");
    searchCriteria.setFilterAndSortingDto(filterAndSortingDto);
    var findListingsResultDto = portfolioService.findListings(searchCriteria, USER);
    assertNotNull(findListingsResultDto);
    var listings = findListingsResultDto.getListings();
    assertEquals(1, findListingsResultDto.getTotalItems());
    assertNotNull(listings);
    assertFalse(listings.isEmpty());
    assertEquals(1, listings.size());
    assertEquals("AAC", listings.get(0).getSymbol());
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
