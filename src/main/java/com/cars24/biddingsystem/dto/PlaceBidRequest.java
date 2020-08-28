package com.cars24.biddingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceBidRequest {
    @NotNull(message = "Bid Amount should not be null")
    private BigDecimal bidAmount;
}
