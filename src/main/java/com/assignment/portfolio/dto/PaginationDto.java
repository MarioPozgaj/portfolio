package com.assignment.portfolio.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class PaginationDto {

  @Min(0)
  private Integer page = 0;

  @Min(0)
  @Max(200)
  private Integer size = 20;

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }
}
