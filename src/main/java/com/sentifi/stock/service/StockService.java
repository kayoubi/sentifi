package com.sentifi.stock.service;

import com.sentifi.stock.domain.SymbolCloseDates;
import com.sentifi.stock.model.Dma;
import com.sentifi.stock.model.DmaResult;
import com.sentifi.stock.model.Price;
import com.sentifi.stock.model.StockPriceResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author khaled
 */
@Service
public class StockService {

    private final QuandlProxyService quandlProxyService;

    public StockService(QuandlProxyService quandlProxyService) {
        this.quandlProxyService = quandlProxyService;
    }

    public StockPriceResult getStocks(final String symbol, final Date startDate, final Date endDate) {
        final SymbolCloseDates result = quandlProxyService.query(symbol, startDate, endDate);
        return new StockPriceResult(Collections.singletonList(new Price(symbol, result.getCloseDates())));
    }

    public DmaResult get200Dma(final String symbol, final Date startDate) {
        final LocalDateTime endDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(200);
        final Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
        final SymbolCloseDates result = quandlProxyService.query(symbol, startDate, endDate);

        final Double zero = Double.parseDouble("0");
        final Double avg = result.getCloseDates().stream().mapToDouble(p -> Double.parseDouble(p.getClose())).average().orElse(zero);
        final String oldestAvailableDate = avg.equals(zero) ? result.getOldestAvailableDate() : null;
        return new DmaResult(new Dma(symbol, avg.toString(), oldestAvailableDate));
    }

    public List<DmaResult> get200Dma(final List<String> symbol, final Date startDate) {
        return symbol.parallelStream().map(s -> get200Dma(s, startDate)).collect(Collectors.toList());
    }
}


