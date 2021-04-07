package com.assignment.portfolio.utils;

import com.assignment.portfolio.dto.PaginationDto;

public interface PaginationUtils {

  static Integer[] getPageIndex(final Integer maxItems, PaginationDto paginationDto) {
    if(paginationDto == null) {
      paginationDto = new PaginationDto();
    }
    var size = paginationDto.getSize();
    var page = Math.max(paginationDto.getPage(), 0);

    if ((size * page) > maxItems) {
      var lowePageIndex = maxItems - size;
      return new Integer[]{Math.max(lowePageIndex, 0), maxItems};
    }

    if (page == 0 && size < maxItems) {
      return new Integer[]{0, size};
    }

    var upperPageIndex = page * size + size >= maxItems
        ? maxItems : page * size + size;
    return new Integer[]{page * size, upperPageIndex};
  }
}
