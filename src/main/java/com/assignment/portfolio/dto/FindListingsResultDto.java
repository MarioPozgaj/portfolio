package com.assignment.portfolio.dto;

import java.util.List;

public class FindListingsResultDto {

  List<ListingDto> listings;
  int totalItems;

  public FindListingsResultDto(List<ListingDto> listings, int totalItems) {
    this.listings = listings;
    this.totalItems = totalItems;
  }

  public List<ListingDto> getListings() {
    return listings;
  }

  public void setListings(List<ListingDto> listings) {
    this.listings = listings;
  }

  public int getTotalItems() {
    return totalItems;
  }

  public void setTotalItems(int totalItems) {
    this.totalItems = totalItems;
  }
}
