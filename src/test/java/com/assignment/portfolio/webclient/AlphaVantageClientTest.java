package com.assignment.portfolio.webclient;

import static com.assignment.portfolio.webclient.AlphaVantageClient.BASE_URL;
import static com.assignment.portfolio.webclient.AlphaVantageClient.LISTINGS_QUERY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;

import java.io.File;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class AlphaVantageClientTest {

  @Value("${api.key}")
  private String apiKey;

  @MockBean
  private RestTemplate restTemplate;

  @Autowired
  private AlphaVantageClient alphaVantageClient;

  @Test
  public void cvsToListingTest() throws FileNotFoundException {
    var file = new File(AlphaVantageClientTest.class.getResource("/listing_status.csv").getFile());
    doReturn(file).when(restTemplate)
        .execute(eq(BASE_URL + LISTINGS_QUERY + apiKey), eq(HttpMethod.GET), isNull(), any());

    var listings = alphaVantageClient.getAllListings();

    assertFalse(listings.isEmpty());
    assertTrue(listings.size() == 3692);
    listings.forEach(listing -> assertEquals("NYSE", listing.getExchange()));
  }

  @Test
  public void cvsToListingTestException() {
    doReturn(null).when(restTemplate)
        .execute(eq(BASE_URL + LISTINGS_QUERY + apiKey), eq(HttpMethod.GET), isNull(), any());

    assertThrows(RuntimeException.class, () -> alphaVantageClient.getAllListings());
  }
}
