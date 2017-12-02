package com.sentifi.stock.service.cache;

import com.sentifi.stock.domain.SymbolCloseDates;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static com.sentifi.stock.TestHelper.getDate;
import static org.junit.Assert.*;

/**
 * @author khaled
 */
public class LeftOverlapCacheExtractorStrategyTest {
    private CachedSymbolCloseDates cache = new CachedSymbolCloseDates(
        getDate("2012-09-01"),
        getDate("2012-09-10"),
        new SymbolCloseDates(
            "FB",
            Arrays.asList(new SymbolCloseDates.CloseDate("2012-9-4", "10"), new SymbolCloseDates.CloseDate("2012-9-2", "16")),
            "2012-10-2"
            )
    );
    private CacheExtractorStrategy strategy = new LeftOverlapCacheExtractorStrategy();

    @Test
    public void testMatchLeftOverlap() {
        assertTrue(strategy._match(cache, getDate("2012-8-1"), getDate("2012-9-5")));
    }

    @Test
    public void testMatchLeftEdge() {
        assertTrue(strategy._match(cache, getDate("2012-08-1"), getDate("2012-9-1")));
    }

    @Test
    public void testNoMatchWhenWithin() {
        assertFalse(strategy._match(cache, getDate("2012-8-1"), getDate("2012-09-12")));
    }

    @Test
    public void testApplyReturnNewCache() {
        Date startDate = getDate("2012-8-1");
        Date endDate = getDate("2012-9-5");
        assertTrue(strategy.match(cache, startDate, endDate));

        SymbolCloseDates closeDates = new SymbolCloseDates(
            "FB",
            Arrays.asList(new SymbolCloseDates.CloseDate("2012-8-7", "19"), new SymbolCloseDates.CloseDate("2012-8-1", "160")),
            "2009-01-01");
        CachedSymbolCloseDates result = strategy.apply(closeDates);

        assertEquals(startDate, result.getStartDate());
        assertEquals(cache.endDate, result.getEndDate());
        assertEquals("2009-01-01", result.cache.getOldestAvailableDate());
        assertEquals(4, result.cache.getCloseDates().size());
        assertEquals("2012-9-4", result.cache.getCloseDates().get(0).getDate());
        assertEquals("2012-9-2", result.cache.getCloseDates().get(1).getDate());
        assertEquals("2012-8-7", result.cache.getCloseDates().get(2).getDate());
        assertEquals("2012-8-1", result.cache.getCloseDates().get(3).getDate());
    }

}