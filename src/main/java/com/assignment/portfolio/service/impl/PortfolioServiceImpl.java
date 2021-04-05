package com.assignment.portfolio.service.impl;

import com.assignment.portfolio.dto.ListingDto;
import com.assignment.portfolio.dto.SearchCriteriaDto;
import com.assignment.portfolio.dto.StockValue;
import com.assignment.portfolio.service.PortfolioService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PortfolioServiceImpl implements PortfolioService {

  @Override
  public List<ListingDto> findListings(final SearchCriteriaDto searchCriteriaDto) {
    return null;
  }

  @Override
  public Boolean subscribeToPortfolio(final String symbol, final String username) {
    return null;
  }

  @Override
  public Boolean unsubscribeToPortfolio(final String symbol, final String username) {
    return null;
  }

  @Override
  public List<StockValue> getUserPortfolio(final String username) {
    return null;
  }
}
