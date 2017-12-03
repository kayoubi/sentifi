package com.sentifi.stock.service;

import com.sentifi.stock.domain.SymbolCloseDates;
import com.sentifi.stock.service.cache.CachedSymbolCloseDates;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;

import static com.sentifi.stock.TestHelper.getDate;
import static com.sentifi.stock.util.DateUtil.addDays;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author khaled
 */
@RunWith(MockitoJUnitRunner.class)
public class QuandlCacheServiceTest {
    @Mock
    private QuandlProxyService proxyService;

    @InjectMocks
    private QuandlCacheService cacheService;

    @Test
    public void testNoCacheWillCallTheProxy() {
        Date startDate = getDate("2012-10-10");
        Date endDate = getDate("2012-10-19");
        String symbol = "FB";

        when(proxyService.query(symbol, startDate, endDate))
            .thenReturn(new SymbolCloseDates(
                symbol,
                Arrays.asList(new SymbolCloseDates.CloseDate("2012-10-14", "10"), new SymbolCloseDates.CloseDate("2012-10-12", "16")),
                "2012-10-2")
            );

        cacheService.query(symbol, startDate, endDate);
        verify(proxyService, times(1)).query(anyString(), any(), any());
        assertEquals(1, cacheService.cache.size());
        assertEquals(startDate, cacheService.cache.get(symbol).getStartDate());
        assertEquals(endDate, cacheService.cache.get(symbol).getEndDate());
        assertEquals(symbol, cacheService.cache.get(symbol).getCache().getSymbol());
        assertEquals(2, cacheService.cache.get(symbol).getCache().getCloseDates().size());
    }

    @Test
    public void testCacheExistWillNoHitTheProxy() {
        Date queryStartDate = getDate("2012-10-10");
        Date queryEndDate = getDate("2012-10-19");
        String symbol = "FB";

        Date cacheStartDate = getDate("2012-10-1");
        Date cacheEndDate = getDate("2012-10-30");
        cacheService.cache.put(symbol, new CachedSymbolCloseDates(cacheStartDate, cacheEndDate,
            new SymbolCloseDates(
                symbol,
                Arrays.asList(new SymbolCloseDates.CloseDate("2012-10-20", "10"), new SymbolCloseDates.CloseDate("2012-10-12", "16")),
                "2012-10-2")
        ));

        SymbolCloseDates result = cacheService.query(symbol, queryStartDate, queryEndDate);
        verify(proxyService, never()).query(anyString(), any(), any());
        assertEquals(symbol, result.getSymbol());
        assertEquals("2012-10-2", result.getOldestAvailableDate());
        assertEquals(1, result.getCloseDates().size());
        assertEquals("2012-10-12", result.getCloseDates().get(0).getDate());

        assertEquals(cacheStartDate, cacheService.cache.get(symbol).getStartDate());
        assertEquals(cacheEndDate, cacheService.cache.get(symbol).getEndDate());
        assertEquals(symbol, cacheService.cache.get(symbol).getCache().getSymbol());
        assertEquals(2, cacheService.cache.get(symbol).getCache().getCloseDates().size());
    }

    @Test
    public void testCacheOverlapWillExpandTheCache() {
        Date queryStartDate = getDate("2012-10-19");
        Date queryEndDate = getDate("2012-11-19");
        String symbol = "FB";

        Date cacheStartDate = getDate("2012-10-1");
        Date cacheEndDate = getDate("2012-10-30");

        when(proxyService.query(symbol, addDays(cacheEndDate, 1), queryEndDate))
            .thenReturn(new SymbolCloseDates(
                symbol,
                Arrays.asList(new SymbolCloseDates.CloseDate("2012-11-3", "10"), new SymbolCloseDates.CloseDate("2012-10-30", "16")),
                "2000-10-2")
            );
        cacheService.cache.put(symbol, new CachedSymbolCloseDates(cacheStartDate, cacheEndDate,
            new SymbolCloseDates(
                symbol,
                Arrays.asList(new SymbolCloseDates.CloseDate("2012-10-20", "10"), new SymbolCloseDates.CloseDate("2012-10-12", "16")),
                "2012-10-2")
        ));

        SymbolCloseDates result = cacheService.query(symbol, queryStartDate, queryEndDate);
        verify(proxyService, times(1)).query(anyString(), any(), any());
        assertEquals(symbol, result.getSymbol());
        assertEquals("2000-10-2", result.getOldestAvailableDate());
        assertEquals(3, result.getCloseDates().size());
        assertEquals("2012-11-3", result.getCloseDates().get(0).getDate());
        assertEquals("2012-10-30", result.getCloseDates().get(1).getDate());
        assertEquals("2012-10-20", result.getCloseDates().get(2).getDate());

        assertEquals(cacheStartDate, cacheService.cache.get(symbol).getStartDate());
        assertEquals(queryEndDate, cacheService.cache.get(symbol).getEndDate());
        assertEquals(symbol, cacheService.cache.get(symbol).getCache().getSymbol());
        assertEquals(4, cacheService.cache.get(symbol).getCache().getCloseDates().size());
    }

}