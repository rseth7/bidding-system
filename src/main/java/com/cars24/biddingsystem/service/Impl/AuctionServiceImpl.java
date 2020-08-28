package com.cars24.biddingsystem.service.Impl;

import com.cars24.biddingsystem.cache.AuctionCacheService;
import com.cars24.biddingsystem.dto.AuctionInfo;
import com.cars24.biddingsystem.dto.PaginationInfo;
import com.cars24.biddingsystem.dto.RunningAuctionResponse;
import com.cars24.biddingsystem.dto.SortInfo;
import com.cars24.biddingsystem.entity.AuctionDetail;
import com.cars24.biddingsystem.entity.AuctionDetailHistory;
import com.cars24.biddingsystem.enums.AuctionState;
import com.cars24.biddingsystem.repository.AuctionDetailHistoryRepository;
import com.cars24.biddingsystem.repository.AuctionDetailRepository;
import com.cars24.biddingsystem.service.AuctionNotificationService;
import com.cars24.biddingsystem.service.AuctionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuctionServiceImpl implements AuctionService {
    @Autowired
    private AuctionDetailRepository auctionDetailRepository;
    @Autowired
    private AuctionNotificationService auctionNotificationService;
    @Autowired
    private AuctionCacheService cacheService;
    @Autowired
    private AuctionDetailHistoryRepository detailHistoryRepository;

    /*
    * Try to place bid against the "RUNNING" auction for a item code
    * */
    @Override
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    public boolean placeBid(final String itemCode, final BigDecimal bidAmount, final String userToken) {
        log.info("Trying to place the bid for user : {} against item code : {}", userToken, itemCode);
        boolean isBidPlaced = false;
        log.info("Populating the auction detail by item code : {} for user : {}", itemCode, userToken);
        Optional<AuctionDetail> mayBeAuctionDetails = auctionDetailRepository.findByItemCode(itemCode);
        if(mayBeAuctionDetails.isPresent()) {
            log.info("Auction detail for item code has been populated from DB for item code : {} and user : {}",
                    itemCode, userToken);
            AuctionDetail auctionDetail = mayBeAuctionDetails.get();
            if(getMinimumEligibleBid(auctionDetail).compareTo(bidAmount) < 0) {
                auctionDetail.setCurrentBidRate(bidAmount);
                //update the bid corresponds to item code
                auctionDetailRepository.save(auctionDetail);
                log.info("Auction Detail has been placed successfully for user : {} against item code : {}",
                        userToken, itemCode);
                persistToAuctionDetailHistory(userToken, auctionDetail);
                updateAuctionCache(itemCode, auctionDetail);
                notifyAllLoggedInUsers(auctionDetail);
                isBidPlaced = true;
            }
        }
        return isBidPlaced;
    }

    private void persistToAuctionDetailHistory(String userToken, AuctionDetail auctionDetail) {
        //persist all bids corresponds to item code
        detailHistoryRepository.save(AuctionDetailHistory.
                getInstance().createAuctionDetailHistory(auctionDetail, userToken));
    }

    private BigDecimal getMinimumEligibleBid(AuctionDetail auctionDetail) {
        return auctionDetail.getCurrentBidRate().add(auctionDetail.getStepRate());
    }

    private void updateAuctionCache(String itemCode, AuctionDetail auctionDetail) {
        //add or update to AuctionCacheService
        CompletableFuture.runAsync(() ->
                cacheService.addOrUpdateToCache(itemCode, auctionDetail));
    }

    private void notifyAllLoggedInUsers(AuctionDetail auctionDetail) {
        //notify to all the users
        CompletableFuture.runAsync(() ->
                auctionNotificationService.setAuctionDetails(auctionDetail));
    }

    /*
    * Populates all the auctions which has "RUNNING" status
    * */
    @Override
    public RunningAuctionResponse getAllRunningAuctions(
            final AuctionState auctionState, final PaginationInfo paginationInfo, final SortInfo sortInfo) {
        log.info("Trying to find all the running auctions");
        Page<AuctionDetail> detailsPage = auctionDetailRepository
                .findByState(auctionState, getPageable(paginationInfo, sortInfo));
        if(detailsPage.getSize() > 0) {
            log.info("Total number of auctions that are currently running is : {}",
                    detailsPage.getTotalElements());
            return getRunningAuctionResponse(detailsPage);
        }
        log.info("Currently auction is not running for any item code");
        return new RunningAuctionResponse(new ArrayList<>(), 0);
    }

    private Pageable getPageable(PaginationInfo paginationInfo, SortInfo sortInfo) {
        Sort.Direction direction = Sort.Direction.valueOf(sortInfo.getSortOrder());
        return PageRequest.of(paginationInfo.getOffset(), paginationInfo.getLimit(),
                Sort.by(direction, sortInfo.getSortBy()));
    }

    private RunningAuctionResponse getRunningAuctionResponse(Page<AuctionDetail> detailsPage) {
        return new RunningAuctionResponse(detailsPage.get()
                .map((auctionDetail) -> AuctionInfo.getInstance()
                        .setItemCode(auctionDetail.getItemCode()).setStepRate(auctionDetail.getStepRate())
                        .setCurrentBidRate(auctionDetail.getCurrentBidRate())).collect(Collectors.toList()),
                detailsPage.getTotalElements());
    }
}
