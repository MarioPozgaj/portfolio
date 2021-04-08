package com.assignment.portfolio.service;

import com.assignment.portfolio.dto.FindListingsResultDto;
import com.assignment.portfolio.dto.SearchCriteriaDto;
import com.assignment.portfolio.webclient.response.TimeSeriesResponse;
import java.util.List;

public interface PortfolioService {

  void getListings();

  FindListingsResultDto findListings(SearchCriteriaDto searchCriteriaDto, String username);

  Boolean subscribeToPortfolio(String symbol, String username);

  Boolean unsubscribeToPortfolio(String symbol, String username);

  List<TimeSeriesResponse> getUserPortfolio(String username);

}
