package com.cars24.biddingsystem.dto;

import com.cars24.biddingsystem.entity.AuctionDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class User {
    private String userToken;

    public User(String userToken) {
        this.userToken = userToken;
    }

    public void notifyAboutRunningAuctions(Object details) {
        AuctionDetail auctionDetail = (AuctionDetail)details;
        log.info("Current bid rate has been updated to : {} for item code : {}",
                auctionDetail.getCurrentBidRate().doubleValue(), auctionDetail.getItemCode());
    }
}
