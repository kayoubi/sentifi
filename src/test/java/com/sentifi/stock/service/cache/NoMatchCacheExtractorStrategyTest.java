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
public class NoMatchCacheExtractorStrategyTest {
    private CachedSymbolCloseDates cache = new CachedSymbolCloseDates(getDate("2012-09-01"), getDate("2012-09-10"), null);
    private CacheExtractorStrategy strategy = new NoMatchCacheExtractorStrategy();

    @Test
    public void testMatchAfterDateRange() {
        assertTrue(strategy._match(cache, getDate("2012-10-1"), getDate("2012-10-30")));
    }

    @Test
    public void testMatchBeforeRange() {
        assertTrue(strategy._match(cache, getDate("2012-08-1"), getDate("2012-08-30")));
    }

    @Test
    public void testNoNoMatchWhenOverlap() {
        assertFalse(strategy._match(cache, getDate("2012-8-1"), getDate("2012-09-01")));
        assertFalse(strategy._match(cache, getDate("2012-8-1"), getDate("2012-09-05")));

        assertFalse(strategy._match(cache, getDate("2012-9-9"), getDate("2012-09-30")));
        assertFalse(strategy._match(cache, getDate("2012-9-10"), getDate("2012-09-30")));
    }

    @Test
    public void testApplyReturnNewCache() {
        Date startDate = getDate("2012-10-1");
        Date endDate = getDate("2012-10-30");
        assertTrue(strategy.match(cache, startDate, endDate));
        SymbolCloseDates closeDates = new SymbolCloseDates(
            "FB",
            Arrays.asList(new SymbolCloseDates.CloseDate("2012-10-1", "10"), new SymbolCloseDates.CloseDate("2012-10-2", "16")),
            "2009-01-01");
        CachedSymbolCloseDates result = strategy.apply(closeDates);

        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals("2009-01-01", result.cache.getOldestAvailableDate());
        assertEquals(2, result.cache.getCloseDates().size());
    }


}
