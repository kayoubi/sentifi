package com.sentifi.stock.controller;

import com.sentifi.stock.exceptions.QuandlException;
import com.sentifi.stock.model.DmaResult;
import com.sentifi.stock.model.StockPriceResult;
import com.sentifi.stock.service.StockService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author khaled
 */
@RestController
@RequestMapping("/api/v2")
public class StockController {
    private final StockService stockService;

    public StockController(final StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping(value = "/{symbol}/closePrice", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public StockPriceResult getStock(
        final @PathVariable String symbol,
        final @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
        final @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        if (startDate.after(new Date())) {
            throw new QuandlException("Start date can't be in the future");
        }
        if (startDate.after(endDate)) {
            throw new QuandlException("Start date can't be after end date");
        }
        return stockService.getStocks(symbol, startDate, endDate);
    }

    @GetMapping(value = "/{symbol}/200dma", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public DmaResult get200Dma(
        final @PathVariable String symbol,
        final @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate) {
        return stockService.get200Dma(symbol, startDate);
    }

    @GetMapping(value = "/200dma/{symbols}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<DmaResult> get200Dma(
        final @PathVariable List<String> symbols,
        final @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate) {

        if (symbols.size() > 1000) {
            throw new QuandlException("Maximum 1000 symbols to query at a time");
        }
        return stockService.get200Dma(symbols, startDate);
    }
}
