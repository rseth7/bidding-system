package com.cars24.biddingsystem.service.Impl;

import com.cars24.biddingsystem.exception.BidException;
import com.cars24.biddingsystem.service.AuctionNotificationService;
import com.cars24.biddingsystem.dto.User;
import com.cars24.biddingsystem.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final Map<String, User> USER_MAP;

    static {
        USER_MAP = new HashMap<>();
    }

    @Autowired
    private AuctionNotificationService auctionNotificationService;

    @PostConstruct
    @Override
    public void loadAllLoggedInUsers() {
        log.info("Populating all the logged-in users");
        InputStream inputStream;
        try {
            Class<?> clazz =  UserServiceImpl.class;
            inputStream = clazz.getResourceAsStream("/static/user.csv");
            readFromInputStream(inputStream);
            //Added Logged in user to realtime broadcast system
            addUserToRealtimeBidBroadcast();
        } catch(Exception e) {
            log.error("Error occurred while populating logged-in users from file with message : {}",
                    e.getMessage(), e);
            throw new BidException("Error while populating logged-in users");
        }
    }

    private void readFromInputStream(final InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                addUser(line);
            }
        }
    }

    private void addUserToRealtimeBidBroadcast() {
        for(User user : USER_MAP.values()) {
            auctionNotificationService.addUser(user);
        }
    }

    @Override
    public boolean addUser(final String userToken) {
        boolean isUserAdded = false;
        try {
            log.info("Trying to add a user with token : {}", userToken);
            USER_MAP.put(userToken, new User(userToken));
            isUserAdded = true;
        } catch(Exception e) {
            log.error("Something went wrong while adding user with message : {}", e.getMessage(), e);
            throw new BidException("Error while adding user");
        }
        return isUserAdded;
    }

    @Override
    public boolean removeUser(final String userToken) {
        boolean isUserRemoved = false;
        try {
            log.info("Trying to remove a user with token : {}", userToken);
            USER_MAP.remove(userToken);
            isUserRemoved = true;
        } catch(Exception e) {
            log.error("Something went wrong while removing user with message : {}", e.getMessage(), e);
            throw new BidException("Error while removing user");
        }
        return isUserRemoved;
    }

    @Override
    public boolean isUserLoggedIn(final String userToken) {
        return USER_MAP.containsKey(userToken);
    }
}
