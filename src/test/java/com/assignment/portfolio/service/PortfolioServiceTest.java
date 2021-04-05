package com.assignment.portfolio.service;

import com.assignment.portfolio.service.impl.PortfolioServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PortfolioServiceTest {

  @Autowired
  PortfolioServiceImpl portfolioService;

  @Test
  public void findListingsTest() {
    portfolioService.getUserPortfolio("user");
  }

}
