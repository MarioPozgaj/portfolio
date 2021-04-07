package com.assignment.portfolio.dto;

public class PaginationDto {

  private int page = 0;
  private int size = 20;

  public PaginationDto() {

  }

  public PaginationDto(int page, int size) {
    this.page = page;
    this.size = size;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void validateAndCorrect() {
    if(page < 0) {
      this.page = 0;
    }

    if(size > 200) {
      this.size = 200;
    }
    if(size < 0) {
      this.size = 0;
    }
  }
}
