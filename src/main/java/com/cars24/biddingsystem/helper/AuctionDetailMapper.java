package com.cars24.biddingsystem.helper;

import com.cars24.biddingsystem.entity.AuctionDetail;
import com.cars24.biddingsystem.entity.AuctionDetailHistory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuctionDetailMapper {
    AuctionDetailMapper INSTANCE = Mappers.getMapper(AuctionDetailMapper.class);
    AuctionDetailHistory toAuctionDetailHistory(final AuctionDetail auctionDetail);
}
