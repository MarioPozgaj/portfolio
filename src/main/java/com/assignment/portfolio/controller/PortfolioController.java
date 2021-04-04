package com.assignment.portfolio.controller;

import com.assignment.portfolio.webclient.AlphaVantageClient;
import java.io.FileNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

  private AlphaVantageClient alphaVantageClient;

  PortfolioController(final AlphaVantageClient alphaVantageClient) {
    this.alphaVantageClient = alphaVantageClient;
  }

  @GetMapping("/all")
  public void getAllListings() throws FileNotFoundException {
    alphaVantageClient.getAllListings();
  }

}
