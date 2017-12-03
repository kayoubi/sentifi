package com.sentifi.stock.controller;

import com.sentifi.stock.domain.SymbolCloseDates;
import com.sentifi.stock.model.Dma;
import com.sentifi.stock.model.DmaResult;
import com.sentifi.stock.model.Price;
import com.sentifi.stock.model.StockPriceResult;
import com.sentifi.stock.service.StockService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;

import static com.sentifi.stock.TestHelper.getDate;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;

/**
 * @author khaled
 */
@WebMvcTest
@RunWith(SpringRunner.class)
public class StockControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Test
    public void testGetStocksOk() throws Exception {
        when(stockService.getStocks("FB", getDate("2012-10-10"), getDate("2012-11-11"))).thenReturn(new StockPriceResult(
            Collections.singletonList(new Price("FB", Arrays.asList(new SymbolCloseDates.CloseDate("2012-10-15", "11.12"), new SymbolCloseDates.CloseDate("2012-10-11", "17.78"))))
        ));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/FB/closePrice?startDate=2012-10-10&endDate=2012-11-11"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.jsonPath("@.Prices.[0].Ticker").value("FB"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.Prices.[0].DateClose.[0].[0]").value("2012-10-15"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.Prices.[0].DateClose.[0].[1]").value("11.12"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.Prices.[0].DateClose.[1].[0]").value("2012-10-11"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.Prices.[0].DateClose.[1].[1]").value("17.78"));
    }

    @Test
    public void testGetStocksBadFormat() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/FB/closePrice?startDate=2012-10-10&endDate=201211-1"))
            .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
            .andExpect(MockMvcResultMatchers.content().string("You provided 201211-1 for endDate. This is not a recognized date format. Please provide yyyy-MM-dd"));
    }

    @Test
    public void testGetStocksMissingParam() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/FB/closePrice?startDate=2012-10-10"))
            .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
            .andExpect(MockMvcResultMatchers.content().string("Required Date parameter 'endDate' is not present"));
    }

    @Test
    public void testGetStocksInvalidRange() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/FB/closePrice?startDate=2012-10-10&endDate=2010-10-10"))
            .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
            .andExpect(MockMvcResultMatchers.content().string("Start date can't be after end date"));
    }

    @Test
    public void testGet200DmaOkNoOldestAvailableDate() throws Exception {
        when(stockService.get200Dma("FB", getDate("2012-10-10"))).thenReturn(new DmaResult(new Dma("FB", "1.23", null)));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/FB/200dma?startDate=2012-10-10"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.jsonPath("@.200dma.Ticker").value("FB"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.200dma.Avg").value("1.23"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.200dma.OldestAvailableDate").doesNotExist());
    }

    @Test
    public void testGet200DmaOkWithOldestAvailableDate() throws Exception {
        when(stockService.get200Dma("FB", getDate("2012-10-10"))).thenReturn(new DmaResult(new Dma("FB", "0.0", "1099-12-12")));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/FB/200dma?startDate=2012-10-10"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.jsonPath("@.200dma.Ticker").value("FB"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.200dma.Avg").value("0.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.200dma.OldestAvailableDate").value("1099-12-12"));
    }

    @Test
    public void testGet200DmaOkMultipleSymbols() throws Exception {
        when(stockService.get200Dma(Arrays.asList("FB", "GE"), getDate("2012-10-10"))).thenReturn(
            Arrays.asList(
                new DmaResult(new Dma("FB", "1.23", null)),
                new DmaResult(new Dma("GE", "0.0", "1099-12-12"))
            )
        );

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v2/200dma/FB,GE?startDate=2012-10-10"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.jsonPath("@", hasSize(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("@.[0].200dma.Ticker").value("FB"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.[0].200dma.Avg").value("1.23"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.[0].200dma.OldestAvailableDate").doesNotExist())
            .andExpect(MockMvcResultMatchers.jsonPath("@.[1].200dma.Ticker").value("GE"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.[1].200dma.Avg").value("0.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("@.[1].200dma.OldestAvailableDate").value("1099-12-12"));
    }

}