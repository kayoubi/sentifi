package com.sentifi.stock.service.cache;

import org.junit.Test;

import static com.sentifi.stock.TestHelper.getDate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author khaled
 */
public class InnerOverlapCacheExtractorStrategyTest {
    private CachedSymbolCloseDates cache = new CachedSymbolCloseDates(getDate("2012-09-01"), getDate("2012-09-10"), null);
    private CacheExtractorStrategy strategy = new InnerOverlapCacheExtractorStrategy();

    @Test
    public void testMatch() {
        assertTrue(strategy._match(cache, getDate("2012-8-1"), getDate("2012-9-30")));
    }

    @Test
    public void testNoMatchLeftEdge() {
        assertFalse(strategy._match(cache, getDate("2012-8-1"), getDate("2012-9-1")));
    }

    @Test
    public void testNoMatchRightEdge() {
        assertFalse(strategy._match(cache, getDate("2012-9-10"), getDate("2012-10-30")));
    }

    @Test
    public void testNoMatchWithin() {
        assertFalse(strategy._match(cache, getDate("2012-9-2"), getDate("2012-9-5")));
    }

}