package com.cars24.biddingsystem.entity;

import com.cars24.biddingsystem.enums.AuctionState;
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
@Table(name = "auction_details",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"item_code"})})
public class AuctionDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
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
    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Version
    private Integer version;

    public AuctionDetail(String itemCode, BigDecimal minBaseRate, BigDecimal currentBidRate,
                         BigDecimal stepRate, AuctionState state) {
        this.itemCode = itemCode;
        this.minBaseRate = minBaseRate;
        this.currentBidRate = currentBidRate;
        this.state = state;
        this.stepRate = stepRate;
        this.createdBy = "SYSTEM";
        this.updatedBy = "SYSTEM";
    }
}
