package com.assignment.portfolio.controller;

import com.assignment.portfolio.dto.ListingDto;
import com.assignment.portfolio.dto.SearchCriteriaDto;
import com.assignment.portfolio.service.PortfolioService;
import com.assignment.portfolio.webclient.response.TimeSeriesResponse;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

  private final PortfolioService portfolioService;

  PortfolioController(final PortfolioService portfolioService) {
    this.portfolioService = portfolioService;
  }

  @GetMapping
  public List<TimeSeriesResponse> getPortfolio(final Authentication authentication) {
    return portfolioService.getUserPortfolio(authentication.getName());
  }

  @PutMapping("/subscribe/{symbol}")
  public Boolean subscribe(final Authentication authentication, @PathVariable final String symbol) {
    return portfolioService.subscribeToPortfolio(authentication.getName(), symbol);
  }

  @PutMapping("/unsubscribe/{symbol}")
  public Boolean unsubscribe(final Authentication authentication, @PathVariable final String symbol) {
    return portfolioService.unsubscribeToPortfolio(authentication.getName(), symbol);
  }

  @GetMapping("/listings")
  public List<ListingDto> findListings(final SearchCriteriaDto searchCriteriaDto) {
    return portfolioService.findListings(searchCriteriaDto);
  }

}
