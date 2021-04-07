package com.assignment.portfolio.utils;

import static com.assignment.portfolio.utils.PaginationUtils.getPageIndex;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.assignment.portfolio.dto.PaginationDto;
import org.junit.jupiter.api.Test;

public class PaginationUtilsTest {

  @Test
  public void test_case_01() {
    var result = getPageIndex(100, new PaginationDto(200, 10));
    assertEquals(90, result[0]);
    assertEquals(100, result[1]);
  }

  @Test
  public void test_case_02() {
    var result = getPageIndex(300, new PaginationDto(200, 200));
    assertEquals(100, result[0]);
    assertEquals(300, result[1]);
  }

  @Test
  public void test_case_03() {
    var result = getPageIndex(300, new PaginationDto(100, 10));
    assertEquals(290, result[0]);
    assertEquals(300, result[1]);
  }

  @Test
  public void test_case_04() {
    var result = getPageIndex(300, new PaginationDto(0, 56));
    assertEquals(0, result[0]);
    assertEquals(56, result[1]);
  }

  @Test
  public void test_case_05() {
    var result = getPageIndex(300, new PaginationDto(0, 100));
    assertEquals(0, result[0]);
    assertEquals(100, result[1]);
  }

  @Test
  public void test_case_06() {
    var result = getPageIndex(300, new PaginationDto(9, 10));
    assertEquals(90, result[0]);
    assertEquals(100, result[1]);
  }

  @Test
  public void test_case_07() {
    var result = getPageIndex(300, new PaginationDto(0, 10));
    assertEquals(0, result[0]);
    assertEquals(10, result[1]);
  }

  @Test
  public void test_case_08() {
    var result = getPageIndex(86, new PaginationDto(1, 50));
    assertEquals(50, result[0]);
    assertEquals(86, result[1]);
  }

  @Test
  public void test_case_09() {
    var result = getPageIndex(157, new PaginationDto(2, 70));
    assertEquals(140, result[0]);
    assertEquals(157, result[1]);
  }

  @Test
  public void test_case_10() {
    var result = getPageIndex(210, new PaginationDto(2, 70));
    assertEquals(140, result[0]);
    assertEquals(210, result[1]);
  }

  @Test
  public void test_case_11() {
    var result = getPageIndex(200, new PaginationDto(1, 70));
    assertEquals(70, result[0]);
    assertEquals(140, result[1]);
  }

  @Test
  public void test_case_12() {
    var result = getPageIndex(200, new PaginationDto(0, 250));
    assertEquals(0, result[0]);
    assertEquals(200, result[1]);
  }

  @Test
  public void test_case_nullPaginationDto() {
    var result = getPageIndex(200, null);
    assertEquals(0, result[0]);
    assertEquals(20, result[1]);
  }

  @Test
  public void test_case_nullValuesInPaginationDto() {
    var result = getPageIndex(200, new PaginationDto(null, null));
    assertEquals(0, result[0]);
    assertEquals(20, result[1]);

    result = getPageIndex(200, new PaginationDto(1, null));
    assertEquals(20, result[0]);
    assertEquals(40, result[1]);

    result = getPageIndex(200, new PaginationDto(null, 50));
    assertEquals(0, result[0]);
    assertEquals(50, result[1]);
  }
}
