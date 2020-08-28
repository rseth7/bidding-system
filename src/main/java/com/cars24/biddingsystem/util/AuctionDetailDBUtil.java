package com.cars24.biddingsystem.util;

import com.cars24.biddingsystem.cache.AuctionCacheService;
import com.cars24.biddingsystem.entity.AuctionDetail;
import com.cars24.biddingsystem.enums.AuctionState;
import com.cars24.biddingsystem.repository.AuctionDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AuctionDetailDBUtil {
    @Autowired
    private AuctionDetailRepository auctionDetailRepository;
    @Autowired
    private AuctionCacheService cacheService;

    private final List<AuctionDetail> auctionDetailList;

    {
        auctionDetailList = new ArrayList<>();
        auctionDetailList.add(new AuctionDetail("X", new BigDecimal(250),
                new BigDecimal(250), new BigDecimal(120), AuctionState.RUNNING));
        auctionDetailList.add(new AuctionDetail("Y", new BigDecimal(250),
                new BigDecimal(500), new BigDecimal(210), AuctionState.RUNNING));
        auctionDetailList.add(new AuctionDetail("Z", new BigDecimal(400),
                new BigDecimal(2500), new BigDecimal(350), AuctionState.RUNNING));
        auctionDetailList.add(new AuctionDetail("U", new BigDecimal(250),
                new BigDecimal(250), new BigDecimal(100), AuctionState.RUNNING));
        auctionDetailList.add(new AuctionDetail("A", new BigDecimal(2500),
                new BigDecimal(7500), new BigDecimal(500), AuctionState.OVER));
        auctionDetailList.add(new AuctionDetail("B", new BigDecimal(250),
                new BigDecimal(5000), new BigDecimal(100), AuctionState.OVER));
    }

    public void loadDataToDB() {
        log.info("loading data to DB and adding to cache simultaneously");
        auctionDetailList.forEach((auctionDetail) -> {
            auctionDetailRepository.save(auctionDetail);
            cacheService.addOrUpdateToCache(auctionDetail.getItemCode(), auctionDetail);
        });
        log.info("Data loaded successfully");
    }
}
