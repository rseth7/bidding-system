package com.cars24.biddingsystem.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@Accessors(chain = true)
public class AuctionInfo {
    private String itemCode;
    private BigDecimal stepRate;
    private BigDecimal currentBidRate;

    public static AuctionInfo getInstance() {
        return new AuctionInfo();
    }
}
