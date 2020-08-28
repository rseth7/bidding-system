package com.cars24.biddingsystem.controller;

import com.cars24.biddingsystem.cache.AuctionCacheService;
import com.cars24.biddingsystem.dto.APIResponse;
import com.cars24.biddingsystem.dto.PaginationInfo;
import com.cars24.biddingsystem.dto.SortInfo;
import com.cars24.biddingsystem.enums.AuctionState;
import com.cars24.biddingsystem.service.AuctionService;
import com.cars24.biddingsystem.service.UserService;
import com.cars24.biddingsystem.util.AppUtils;
import com.cars24.biddingsystem.util.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/auction")
@Validated
@Slf4j
public class AuctionController {
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuctionCacheService cacheService;

    @PostMapping(value = "/{itemCode}/bid")
    public APIResponse placeBid(@RequestParam BigDecimal bidAmount, @PathVariable("itemCode") String itemCode,
                                @RequestHeader("userToken") String userToken) throws IOException {
        log.info("Placing a bid for user : {} against the item code : {}", userToken, itemCode);
        //User validation
        if(!userService.isUserLoggedIn(userToken)) {
            log.error("User : {} is currently not logged in", userToken);
            return AppUtils
                    .populateAPIResponse(ResponseMessage.INVALID_USER, HttpStatus.UNAUTHORIZED, null);
        }
        //Whether item code is available for bid
        if(!cacheService.isItemCodeAvailable(itemCode)) {
            log.error("Item code : {} is not available", itemCode);
            return AppUtils.populateAPIResponse(ResponseMessage.AUCTION_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        //Checks whether place bid amount should greater than current bid and should be currently "RUNNING"
        if(!(cacheService
                .validateCurrentBidRateRateAndState(itemCode, bidAmount))) {
            log.error("Either auction is not Running for given item code or bid amount may be" +
                    " less than the current bid rate for item code : {}", itemCode);
            return AppUtils.populateAPIResponse(ResponseMessage.AUCTION_NOT_RUNNING_OR_PLACE_BID_IS_INVALID, HttpStatus.NOT_FOUND);
        }
        //Place bid request
        if(!auctionService.placeBid(itemCode, bidAmount, userToken)) {
            log.error("Not able to place the bid for user : {} against the item code : {}",
                    userToken, itemCode);
            return AppUtils.populateAPIResponse(ResponseMessage.BID_REJECTED, HttpStatus.NOT_ACCEPTABLE);
        }
        log.info("Bid has been placed successfully for user : {} against item code : {}",
                userToken, itemCode);
        return AppUtils.populateAPIResponse(ResponseMessage.BID_ACCEPTED, HttpStatus.CREATED);
    }

    @GetMapping
    public APIResponse getAllRunningAuction(@RequestParam("state") AuctionState auctionState,
                                                       @RequestHeader(value = "offset", defaultValue = "0", required = false) int offset,
                                                       @RequestHeader(value = "limit", defaultValue = "10", required = false) int limit,
                                                       @RequestHeader(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
                                                       @RequestHeader(value = "sortOrder", defaultValue = "DESC", required = false) String sortOrder) {
        log.info("Populating all the bids which are currently in {} state", auctionState.name());
        PaginationInfo paginationInfo = new PaginationInfo(offset, limit);
        SortInfo sortInfo = new SortInfo(sortBy, sortOrder);
        return AppUtils.populateAPIResponse(ResponseMessage.SUCCESS, HttpStatus.OK,
                auctionService.getAllRunningAuctions(auctionState, paginationInfo, sortInfo));
    }
}
