package com.assignment.portfolio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RestClientException extends RuntimeException {

  public RestClientException(String message) {
    super(message);
  }

}
