package com.yapp.backend.common.exception;

import lombok.Getter;

@Getter

public class UserNotFoundException extends CustomException {
  private final ErrorCode errorCode;

  public UserNotFoundException(ErrorCode errorCode) {
    super(errorCode);
    this.errorCode = errorCode;
  }
}
