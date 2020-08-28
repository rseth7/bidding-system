package com.cars24.biddingsystem.util;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    BID_ACCEPTED("M0001", "Bid is accepted"),
    BID_REJECTED("M0002", "Bid is rejected"),
    AUCTION_NOT_FOUND("M0003", "Auction not found"),
    INVALID_USER("M0004", "User not logged in or invalid user"),
    SUCCESS("M0005", "SUCCESS"),
    FAILURE("M0006", "FAILURE"),
    AUCTION_NOT_RUNNING_OR_PLACE_BID_IS_INVALID("M0007", "Either auction is not in 'RUNNING' state" +
            " or may be place bid is smaller than the current bid for a given item code");

    private final String code;
    private final String message;

    ResponseMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
