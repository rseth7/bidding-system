package com.cars24.biddingsystem.listener;

import com.cars24.biddingsystem.util.AuctionDetailDBUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class BiddingSystemContextListener implements ServletContextListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(BiddingSystemContextListener.class);
    @Autowired
    private AuctionDetailDBUtil detailDBUtil;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Initializing Bidding System Context");
        detailDBUtil.loadDataToDB();
        LOGGER.info("Bidding System Context Initialized Successfully");
    }
}
