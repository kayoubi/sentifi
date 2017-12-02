package com.sentifi.stock.controller;

import com.sentifi.stock.exceptions.QuandlException;
import com.sentifi.stock.model.DmaResult;
import com.sentifi.stock.model.StockPriceResult;
import com.sentifi.stock.service.StockService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

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

    @GetMapping("/api/v2/{symbol}/200dma")
    public DmaResult get200Dma(
        final @PathVariable String symbol,
        final @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate) {
        return stockService.get200Dma(symbol, startDate);
    }

    @GetMapping("/api/v2/200dma/{symbols}")
    public List<DmaResult> get200Dma(
        final @PathVariable List<String> symbols,
        final @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate) {

        if (symbols.size() > 1000) {
            throw new QuandlException("Maximum 1000 symbols to query at a time");
        }
        return stockService.get200Dma(symbols, startDate);
    }
}
