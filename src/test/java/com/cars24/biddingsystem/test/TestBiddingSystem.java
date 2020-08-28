package com.cars24.biddingsystem.test;

import com.cars24.biddingsystem.BiddingSystemApplicationTests;
import com.cars24.biddingsystem.cache.AuctionCacheService;
import com.cars24.biddingsystem.dto.AuctionInfo;
import com.cars24.biddingsystem.dto.PaginationInfo;
import com.cars24.biddingsystem.dto.RunningAuctionResponse;
import com.cars24.biddingsystem.dto.SortInfo;
import com.cars24.biddingsystem.entity.AuctionDetail;
import com.cars24.biddingsystem.enums.AuctionState;
import com.cars24.biddingsystem.repository.AuctionDetailRepository;
import com.cars24.biddingsystem.service.AuctionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestBiddingSystem extends BiddingSystemApplicationTests {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @Autowired
    private AuctionDetailRepository auctionDetailRepository;
    @MockBean
    private AuctionService mockAuctionService;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private AuctionCacheService cacheService;

    public List<AuctionDetail> loadSetUpData() {
        List<AuctionDetail> auctionDetailList = new ArrayList<>();
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
        return auctionDetailList;
    }

    @Before
    public void setup() {
        log.info("Data loading started");
        List<AuctionDetail> auctionDetailList = loadSetUpData();
        auctionDetailList.forEach((auctionDetail) -> {
            auctionDetailRepository.save(auctionDetail);
            cacheService.addOrUpdateToCache(auctionDetail.getItemCode(), auctionDetail);
        });
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        log.info("Loading done");
    }

    @After
    public void cleanUp() {
        log.info("clean up started");
        auctionDetailRepository.deleteAll();
        log.info("clean up done");
    }

    @Test
    public void whenRunningAuctionStateIsProvided_thenRunningAuctionResponseIsCorrect() throws Exception {
        AuctionDetail a1 = new AuctionDetail("X", new BigDecimal(250),
                new BigDecimal(250), new BigDecimal(120), AuctionState.RUNNING);
        AuctionDetail a2 = new AuctionDetail("Y", new BigDecimal(250),
                new BigDecimal(500), new BigDecimal(210), AuctionState.RUNNING);
        AuctionDetail a3 = new AuctionDetail("Z", new BigDecimal(400),
                new BigDecimal(2500), new BigDecimal(350), AuctionState.RUNNING);
        AuctionDetail a4 = new AuctionDetail("U", new BigDecimal(250),
                new BigDecimal(250), new BigDecimal(100), AuctionState.RUNNING);

        PaginationInfo paginationInfo = new PaginationInfo(0, 10);
        SortInfo sortInfo = new SortInfo("createdAt", "DESC");
        List<AuctionInfo> auctionInfoList = new ArrayList<>();
        auctionInfoList.add(new AuctionInfo().setItemCode(a1.getItemCode())
                .setStepRate(a1.getStepRate()).setCurrentBidRate(a1.getCurrentBidRate()));
        auctionInfoList.add(new AuctionInfo().setItemCode(a2.getItemCode())
                .setStepRate(a2.getStepRate()).setCurrentBidRate(a2.getCurrentBidRate()));
        auctionInfoList.add(new AuctionInfo().setItemCode(a3.getItemCode())
                .setStepRate(a3.getStepRate()).setCurrentBidRate(a3.getCurrentBidRate()));
        auctionInfoList.add(new AuctionInfo().setItemCode(a4.getItemCode())
                .setStepRate(a4.getStepRate()).setCurrentBidRate(a4.getCurrentBidRate()));
        RunningAuctionResponse expectedAuctionResponse = new RunningAuctionResponse(auctionInfoList, auctionInfoList.size());
        BDDMockito.given(auctionService.getAllRunningAuctions(AuctionState.RUNNING, paginationInfo, sortInfo))
                .willReturn(expectedAuctionResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/auction?state=RUNNING"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("M0005"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"));

        RunningAuctionResponse actualRunningAuctionResponse = auctionService.getAllRunningAuctions(AuctionState.RUNNING, paginationInfo, sortInfo);
        Assert.assertEquals(expectedAuctionResponse, actualRunningAuctionResponse);
    }

    @Test
    public void whenOverAuctionStateIsProvided_thenOverAuctionResponseIsCorrect() throws Exception {
        AuctionDetail a1 = new AuctionDetail("A", new BigDecimal(2500),
                new BigDecimal(7500), new BigDecimal(500), AuctionState.OVER);
        AuctionDetail a2 = new AuctionDetail("B", new BigDecimal(250),
                new BigDecimal(5000), new BigDecimal(100), AuctionState.OVER);

        PaginationInfo paginationInfo = new PaginationInfo(0, 10);
        SortInfo sortInfo = new SortInfo("createdAt", "DESC");
        List<AuctionInfo> auctionInfoList = new ArrayList<>();
        auctionInfoList.add(new AuctionInfo().setItemCode(a1.getItemCode())
                .setStepRate(a1.getStepRate()).setCurrentBidRate(a1.getCurrentBidRate()));
        auctionInfoList.add(new AuctionInfo().setItemCode(a2.getItemCode())
                .setStepRate(a2.getStepRate()).setCurrentBidRate(a2.getCurrentBidRate()));
        RunningAuctionResponse expectedAuctionResponse = new RunningAuctionResponse(auctionInfoList, auctionInfoList.size());
        BDDMockito.given(auctionService.getAllRunningAuctions(AuctionState.OVER, paginationInfo, sortInfo))
                .willReturn(expectedAuctionResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/auction?state=OVER"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("M0005"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"));

        RunningAuctionResponse actualRunningAuctionResponse = auctionService.getAllRunningAuctions(AuctionState.OVER, paginationInfo, sortInfo);
        Assert.assertEquals(expectedAuctionResponse, actualRunningAuctionResponse);
    }

    @Test
    public void whenWrongItemCodeIsProvided_thenNotFoundResponseIsCorrect() throws Exception {
        boolean expectedPlaceBidResponse = false;
        BDDMockito.given(auctionService.placeBid("E", new BigDecimal(500), "token1"))
                .willReturn(expectedPlaceBidResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/auction/E/bid")
                .header("userToken", "token1").param("bidAmount", "500"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("M0003"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Auction not found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("NOT_FOUND"));

        boolean actualPlaceBidResponse = auctionService.placeBid("E", new BigDecimal(500), "token1");
        Assert.assertEquals(expectedPlaceBidResponse, actualPlaceBidResponse);
    }

    @Test
    public void whenBidRateIsLessThanCurrentBidRateIsProvided_thenNotFoundResponseIsCorrect() throws Exception {
        boolean expectedPlaceBidResponse = false;
        BDDMockito.given(auctionService.placeBid("Y", new BigDecimal(100), "token1"))
                .willReturn(expectedPlaceBidResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/auction/Y/bid")
                .header("userToken", "token1").param("bidAmount", "100"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("M0007"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Either auction is not in 'RUNNING' state" +
                        " or may be place bid is smaller than the current bid for a given item code"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("NOT_FOUND"));

        boolean actualPlaceBidResponse = auctionService.placeBid("Y", new BigDecimal(100), "token1");
        Assert.assertEquals(expectedPlaceBidResponse, actualPlaceBidResponse);
    }

    @Test
    public void whenCorrectBidIsProvided_thenCreatedResponseIsCorrect() throws Exception {
        boolean expectedPlaceBidResponse = true;
        BDDMockito.given(auctionService.placeBid("Y", new BigDecimal(900), "token1"))
                .willReturn(expectedPlaceBidResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/auction/Y/bid")
                .header("userToken", "token1").param("bidAmount", "900"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("M0001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Bid is accepted"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("CREATED"));

        boolean actualPlaceBidResponse = auctionService.placeBid("Y", new BigDecimal(900), "token1");
        Assert.assertEquals(expectedPlaceBidResponse, actualPlaceBidResponse);
    }

    @Test
    public void whenIncorrectBidIsProvided_thenRejectedResponseIsCorrect() throws Exception {
        boolean expectedPlaceBidResponse = false;
        BDDMockito.given(auctionService.placeBid("X", new BigDecimal(270), "token1"))
                .willReturn(expectedPlaceBidResponse);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/auction/X/bid")
                .header("userToken", "token1").param("bidAmount", "270"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("M0002"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Bid is rejected"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("NOT_ACCEPTABLE"));

        boolean actualPlaceBidResponse = auctionService.placeBid("X", new BigDecimal(270), "token1");
        Assert.assertEquals(expectedPlaceBidResponse, actualPlaceBidResponse);
    }
}
