package com.cars24.biddingsystem.repository;

import com.cars24.biddingsystem.entity.AuctionDetailHistory;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionDetailHistoryRepository extends PagingAndSortingRepository<AuctionDetailHistory, Long> {
}
