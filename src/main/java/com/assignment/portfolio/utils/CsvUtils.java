package com.assignment.portfolio.utils;

import com.assignment.portfolio.dto.ListingDto;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;

public interface CsvUtils {

  String COMMA_DELIMITER = ",";
  String NYSE = "NYSE";
  DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  static List<ListingDto> getListingsFromCsv(File file) throws FileNotFoundException {
    var listings = new ArrayList<ListingDto>();

    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        var listing = getRecordFromLine(scanner.nextLine());
        if(listing != null && StringUtils.equals(listing.getExchange(), NYSE))
          listings.add(listing);
      }
    }
    return listings;
  }

  private static ListingDto getRecordFromLine(String line) {
    List<String> values = new ArrayList<>();
    try (Scanner rowScanner = new Scanner(line)) {
      rowScanner.useDelimiter(COMMA_DELIMITER);
      while (rowScanner.hasNext()) {
        values.add(rowScanner.next());
      }
    }
    if(StringUtils.equals(values.get(4), "ipoDate")) {
      return null;
    }
    return getListing(values);
  }

  private static ListingDto getListing(List<String> values) {
    var listing = new ListingDto();
    listing.setSymbol(values.get(0));
    listing.setName(values.get(1));
    listing.setExchange(values.get(2));
    listing.setAssetType(values.get(3));
    listing.setIpoData(stringToDate(values.get(4)));
    listing.setDelistingDate(stringToDate(values.get(5)));
    listing.setStatus(values.get(6));
    return listing;
  }

  private static LocalDate stringToDate(String stringDate) {
    if(StringUtils.isNotBlank(stringDate) && !StringUtils.equals(stringDate, "null")) {
      return LocalDate.parse(stringDate, FORMATTER);
    }
    return null;
  }

}
