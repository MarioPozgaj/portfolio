package com.assignment.portfolio.service.impl;

import com.assignment.portfolio.dto.ListingDto;
import com.assignment.portfolio.dto.SearchCriteriaDto;
import com.assignment.portfolio.service.PortfolioService;
import com.assignment.portfolio.webclient.AlphaVantageClient;
import com.assignment.portfolio.webclient.response.TimeSeriesResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class PortfolioServiceImpl implements PortfolioService {

  private AlphaVantageClient alphaVantageClient;

  private static final Map<String, Set<String>> userSubscriptions = new ConcurrentHashMap<>();
  private static List<ListingDto> listings;

  public PortfolioServiceImpl(final AlphaVantageClient alphaVantageClient) {
    this.alphaVantageClient = alphaVantageClient;
  }

  public void getListings() {
    listings = alphaVantageClient.getAllListings();
  }

  @Override
  public List<ListingDto> findListings(final SearchCriteriaDto searchCriteriaDto) {

    return null;
  }

  @Override
  public Boolean subscribeToPortfolio(final String symbol, final String username) {
    checkIfUserSubscriptionExists(username);
    var subscriptions = userSubscriptions.get(username);
    if(listings == null) {
      getListings();
    }
    final var optionalListing = listings.stream()
        .filter(listing -> StringUtils.equals(listing.getSymbol(), symbol)).findAny();
    if (optionalListing.isPresent()) {
      subscriptions.add(symbol);
      return true;
    }
    return false;
  }

  @Override
  public Boolean unsubscribeToPortfolio(final String symbol, final String username) {
    checkIfUserSubscriptionExists(username);
    var subscriptions = userSubscriptions.get(username);
    return subscriptions.remove(symbol);
  }

  @Override
  public List<TimeSeriesResponse> getUserPortfolio(final String username) {
    var subscriptions = userSubscriptions.get(username);
    if (ObjectUtils.isEmpty(subscriptions)) {
      return Collections.emptyList();
    }

    var stockValues = new ArrayList<TimeSeriesResponse>();
    subscriptions.forEach(symbol -> stockValues.add(alphaVantageClient.getStockValues(symbol)));
    return stockValues;
  }

  private void checkIfUserSubscriptionExists(String username) {
    if (!userSubscriptions.containsKey(username)) {
      userSubscriptions.put(username, ConcurrentHashMap.newKeySet());
    }
  }
}
