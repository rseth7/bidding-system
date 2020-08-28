package com.cars24.biddingsystem.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BidException extends RuntimeException {
    public BidException() {
        super();
    }

    public BidException(String message) {
        super(message);
    }

    public BidException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
