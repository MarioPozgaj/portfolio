package com.assignment.portfolio.service.impl;

import static com.assignment.portfolio.utils.PaginationUtils.getPageIndex;

import com.assignment.portfolio.dto.FilterAndSortingDto;
import com.assignment.portfolio.dto.FilterAndSortingDto.Direction;
import com.assignment.portfolio.dto.ListingDto;
import com.assignment.portfolio.dto.PaginationDto;
import com.assignment.portfolio.dto.SearchCriteriaDto;
import com.assignment.portfolio.service.PortfolioService;
import com.assignment.portfolio.webclient.AlphaVantageClient;
import com.assignment.portfolio.webclient.response.TimeSeriesResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
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

  @Override
  public void getListings() {
    listings = alphaVantageClient.getAllListings();
  }

  @Override
  public List<ListingDto> findListings(final SearchCriteriaDto searchCriteriaDto, final String username) {
    checkIfListingsExist();

    var pagination = searchCriteriaDto.getPaginationDto();
    if (pagination == null) {
      pagination = new PaginationDto();
    }

    var filterAndSorting = searchCriteriaDto.getFilterAndSortingDto();

    if (filterAndSorting == null) {
      filterAndSorting = new FilterAndSortingDto();
    }

    List<ListingDto> filteredAndPaginatedListings = null;
    if (StringUtils.isNotBlank(filterAndSorting.getFilterValue())
        && StringUtils.isNotBlank(filterAndSorting.getFilterField())) {
      final var filterValue = filterAndSorting.getFilterValue().toUpperCase();
      switch (filterAndSorting.getFilterField()) {
        case "symbol":
          filteredAndPaginatedListings = listings.stream()
              .filter(listing -> StringUtils.contains(listing.getSymbol().toUpperCase(), filterValue))
              .collect(Collectors.toList());
          break;
        case "name":
          filteredAndPaginatedListings = listings.stream()
              .filter(listing -> StringUtils.contains(listing.getName().toUpperCase(), filterValue))
              .collect(Collectors.toList());
          break;
      }
    }

    if (StringUtils.isNotBlank(filterAndSorting.getSortingField())) {
      if (filteredAndPaginatedListings == null) {
        filteredAndPaginatedListings = new ArrayList<>();
        filteredAndPaginatedListings.addAll(listings);
      }
      final var sortingField = filterAndSorting.getSortingField();
      switch (sortingField) {
        case "symbol": {
          if (Direction.DESC.equals(filterAndSorting.getSortDirection())) {
            filteredAndPaginatedListings
                .sort(Comparator.comparing(ListingDto::getSymbol).reversed());
          } else {
            filteredAndPaginatedListings.sort(Comparator.comparing(ListingDto::getSymbol));
          }
          break;
        }
        case "name": {
          if (Direction.DESC.equals(filterAndSorting.getSortDirection())) {
            filteredAndPaginatedListings
                .sort(Comparator.comparing(ListingDto::getName).reversed());
          } else {
            filteredAndPaginatedListings.sort(Comparator.comparing(ListingDto::getName));
          }
          break;
        }
      }
    }
    if (filteredAndPaginatedListings == null) {
      filteredAndPaginatedListings.addAll(listings);
    }
    var index = getPageIndex(filteredAndPaginatedListings.size(), pagination);
    var list = filteredAndPaginatedListings.subList(index[0], index[1]);
    var subscriptions = userSubscriptions.get(username);

    list.forEach(listing -> {
      if (ObjectUtils.isNotEmpty(subscriptions)) {
        listing.setSubscribed(subscriptions.contains(listing.getSymbol()));
      } else {
        listing.setSubscribed(false);
      }
    });

    return list;
  }

  @Override
  public Boolean subscribeToPortfolio(final String symbol, final String username) {
    checkIfUserSubscriptionExists(username);
    var subscriptions = userSubscriptions.get(username);
    checkIfListingsExist();
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

  private void checkIfUserSubscriptionExists(final String username) {
    if (!userSubscriptions.containsKey(username)) {
      userSubscriptions.put(username, ConcurrentHashMap.newKeySet());
    }
  }

  private void checkIfListingsExist() {
    if (listings == null) {
      getListings();
    }
  }
}
