package com.cars24.biddingsystem.repository;

import com.cars24.biddingsystem.entity.AuctionDetail;
import com.cars24.biddingsystem.enums.AuctionState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuctionDetailRepository extends CrudRepository<AuctionDetail, Long> {
    Optional<AuctionDetail> findByItemCode(final String itemCode);
    Page<AuctionDetail> findByState(final AuctionState state, Pageable pageable);
}
