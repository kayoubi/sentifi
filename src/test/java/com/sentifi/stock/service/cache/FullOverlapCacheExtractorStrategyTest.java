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
public class FullOverlapCacheExtractorStrategyTest {
    private CachedSymbolCloseDates cache = new CachedSymbolCloseDates(
        getDate("2012-09-01"),
        getDate("2012-09-10"),
        new SymbolCloseDates(
            "FB",
            Arrays.asList(new SymbolCloseDates.CloseDate("2012-9-4", "10"), new SymbolCloseDates.CloseDate("2012-9-2", "16")),
            "2012-10-2"
        )
    );
    private CacheExtractorStrategy strategy = new FullOverlapCacheExtractorStrategy();

    @Test
    public void testMatchOverlap() {
        assertTrue(strategy._match(cache, getDate("2012-9-5"), getDate("2012-9-7")));
    }

    @Test
    public void testMatchLeftEdge() {
        assertTrue(strategy._match(cache, getDate("2012-09-1"), getDate("2012-9-8")));
    }

    @Test
    public void testMatchRightEdge() {
        assertTrue(strategy._match(cache, getDate("2012-09-6"), getDate("2012-9-10")));
    }

    @Test
    public void testNoMatchWhenOutsideLef() {
        assertFalse(strategy._match(cache, getDate("2012-8-1"), getDate("2012-09-9")));
    }

    @Test
    public void testNoMatchWhenOutsideRight() {
        assertFalse(strategy._match(cache, getDate("2012-9-1"), getDate("2012-09-12")));
    }

    @Test
    public void testApplyReturnNewCache() {
        Date startDate = getDate("2012-9-5");
        Date endDate = getDate("2012-9-7");
        assertTrue(strategy.match(cache, startDate, endDate));

        CachedSymbolCloseDates result = strategy.apply(null);

        assertEquals(cache.startDate, result.getStartDate());
        assertEquals(cache.endDate, result.getEndDate());
        assertNull(strategy.getStartDate());
        assertNull(strategy.getEndDate());
        assertEquals("2012-10-2", result.cache.getOldestAvailableDate());
        assertEquals(2, result.cache.getCloseDates().size());
        assertEquals("2012-9-4", result.cache.getCloseDates().get(0).getDate());
        assertEquals("2012-9-2", result.cache.getCloseDates().get(1).getDate());
    }

}