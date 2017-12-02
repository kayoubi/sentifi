package com.sentifi.stock.controller;

import com.sentifi.stock.model.StockPriceResult;
import com.sentifi.stock.service.StockService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author khaled
 */
@RestController
public class StockController {
    private final StockService stockService;

    public StockController(final StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/api/v2/{symbol}/closePrice")
    public StockPriceResult getStock(
        final @PathVariable String symbol,
        final @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
        final @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        return stockService.getStocks(symbol, startDate, endDate);
    }
}
