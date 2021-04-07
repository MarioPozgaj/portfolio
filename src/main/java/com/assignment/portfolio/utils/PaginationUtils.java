package com.assignment.portfolio.utils;

import com.assignment.portfolio.dto.PaginationDto;

public interface PaginationUtils {

  static Integer[] getPageIndex(final Integer maxItems, PaginationDto paginationDto) {
    if(paginationDto == null) {
      paginationDto = new PaginationDto();
    }
    var size = paginationDto.getSize() == null ? 20 : paginationDto.getSize();
    var page = paginationDto.getPage() == null || paginationDto.getPage() < 0
        ? 0 : paginationDto.getPage();

    if ((size * page) > maxItems) {
      return new Integer[]{maxItems - size, maxItems};
    }

    if (page == 0 && size < maxItems) {
      return new Integer[]{0, size};
    }

    var upperPageIndex = page * size + size >= maxItems
        ? maxItems : page * size + size;
    return new Integer[]{page * size, upperPageIndex};
  }
}
