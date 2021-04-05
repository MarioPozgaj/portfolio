package com.assignment.portfolio.dto;

public class SearchCriteriaDto {

  private PaginationDto paginationDto;

  private  FilterAndSortingDto filterAndSortingDto;

  public PaginationDto getPaginationDto() {
    return paginationDto;
  }

  public void setPaginationDto(PaginationDto paginationDto) {
    this.paginationDto = paginationDto;
  }

  public FilterAndSortingDto getFilterAndSortingDto() {
    return filterAndSortingDto;
  }

  public void setFilterAndSortingDto(FilterAndSortingDto filterAndSortingDto) {
    this.filterAndSortingDto = filterAndSortingDto;
  }
}
