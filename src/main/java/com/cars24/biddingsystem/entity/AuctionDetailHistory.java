package com.cars24.biddingsystem.entity;

import com.cars24.biddingsystem.enums.AuctionState;
import com.cars24.biddingsystem.helper.AuctionDetailMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "auction_detail_history")
public class AuctionDetailHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_token")
    private String userToken;
    @Column(name = "item_code")
    private String itemCode;
    @Column(name = "step_rate")
    private BigDecimal stepRate;
    @Column(name = "minimum_base_rate")
    private BigDecimal minBaseRate;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private AuctionState state;
    @Column(name = "current_bid_rate")
    private BigDecimal currentBidRate;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;

    public static AuctionDetailHistory getInstance() {
        return new AuctionDetailHistory();
    }
    public AuctionDetailHistory createAuctionDetailHistory(final AuctionDetail detail, final String userToken) {
        AuctionDetailHistory detailHistory = AuctionDetailMapper.INSTANCE.toAuctionDetailHistory(detail);
        detailHistory.setUserToken(userToken);
        return detailHistory;
    }
}
