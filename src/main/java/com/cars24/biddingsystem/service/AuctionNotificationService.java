package com.cars24.biddingsystem.service;

import com.cars24.biddingsystem.dto.User;
import com.cars24.biddingsystem.entity.AuctionDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AuctionNotificationService {
    private final List<User> userList = new ArrayList<>();

    public void addUser(final User user) {
        this.userList.add(user);
    }

    public void removeUser(final User user) {
        this.userList.remove(user);
    }

    public void setAuctionDetails(final AuctionDetail auctionDetail) {
        log.info("Notifying all the logged-in users about the updated bid against the item code");
        for(User user : this.userList) {
            user.notifyAboutRunningAuctions(auctionDetail);
            log.info("user : {} has been notified for item code : {}",
                    user.getUserToken(), auctionDetail.getItemCode());
        }
    }
}
