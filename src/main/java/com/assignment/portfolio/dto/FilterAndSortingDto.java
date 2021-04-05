package com.assignment.portfolio.dto;

public class FilterAndSortingDto {

  private String sortingField = "name";
  private Direction sortDirection = Direction.ASC;
  private String filterField = "name";
  private String filterValue;

  public String getSortingField() {
    return sortingField;
  }

  public void setSortingField(String sortingField) {
    this.sortingField = sortingField;
  }

  public Direction getSortDirection() {
    return sortDirection;
  }

  public void setSortDirection(Direction sortDirection) {
    this.sortDirection = sortDirection;
  }

  public String getFilterField() {
    return filterField;
  }

  public void setFilterField(String filterField) {
    this.filterField = filterField;
  }

  public String getFilterValue() {
    return filterValue;
  }

  public void setFilterValue(String filterValue) {
    this.filterValue = filterValue;
  }

  public enum Direction {
    ASC, DESC;
  }

}
