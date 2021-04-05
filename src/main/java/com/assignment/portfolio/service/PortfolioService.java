package com.assignment.portfolio.service;

import com.assignment.portfolio.dto.ListingDto;
import com.assignment.portfolio.dto.SearchCriteriaDto;
import com.assignment.portfolio.webclient.response.TimeSeriesResponse;
import java.util.List;

public interface PortfolioService {

  List<ListingDto> findListings(SearchCriteriaDto searchCriteriaDto);

  Boolean subscribeToPortfolio(String symbol, String username);

  Boolean unsubscribeToPortfolio(String symbol, String username);

  List<TimeSeriesResponse> getUserPortfolio(String username);

}
