package com.cars24.biddingsystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RunningAuctionResponse {
    private List<AuctionInfo> auctionInfoList;
    private Long totalAvailableAuctions;

    public RunningAuctionResponse(final List<AuctionInfo> auctionInfoList, final long totalAvailableAuctions) {
        this.totalAvailableAuctions = totalAvailableAuctions;
        this.auctionInfoList = auctionInfoList;
    }
}
