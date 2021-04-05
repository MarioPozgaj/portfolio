package com.assignment.portfolio.service;

import com.assignment.portfolio.dto.ListingDto;
import com.assignment.portfolio.dto.SearchCriteriaDto;
import com.assignment.portfolio.dto.StockValue;
import java.util.List;

public interface PortfolioService {

  List<ListingDto> findListings(SearchCriteriaDto searchCriteriaDto);

  void subscribeToPortfolio(String symbol);

  void unsubscribeToPortfolio(String symbol);

  List<StockValue> getUserPortfolio(String username);

}
