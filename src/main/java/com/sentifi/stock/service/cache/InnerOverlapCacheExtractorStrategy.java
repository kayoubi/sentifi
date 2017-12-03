package com.sentifi.stock.service.cache;

import java.util.Date;

/**
 * A strategy to match the case where the cache is within the request dates boundaries, and we need to get new result
 *
 * request start date is before the start date of the cache and request end date is after the end date of the cache
 *
 * In this case we'll request the new data ane replace the cache
 *
 * @see NoMatchCacheExtractorStrategy
 *
 * @author khaled
 */
public class InnerOverlapCacheExtractorStrategy extends NoMatchCacheExtractorStrategy {
    @Override
    public boolean _match(CachedSymbolCloseDates cache, Date startDate, Date endDate) {
        return startDate.before(cache.startDate) && endDate.after(cache.endDate);
    }
}
