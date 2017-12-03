package com.sentifi.stock.service.cache;

import com.sentifi.stock.domain.SymbolCloseDates;

import java.util.Date;

import static com.sentifi.stock.util.DateUtil.isBetween;

/**
 * A strategy to match the case where the request date range already in the cache
 *
 * in this case we just return the cache as is to be filtered
 *
 * @author khaled
 */
public class FullOverlapCacheExtractorStrategy extends CacheExtractorStrategy {
    @Override
    public boolean _match(CachedSymbolCloseDates cache, Date startDate, Date endDate) {
        return isBetween(startDate, cache.startDate, cache.endDate)
            && isBetween(endDate, cache.startDate, cache.endDate);
    }

    @Override
    public Date getStartDate() {
        return null;
    }

    @Override
    public Date getEndDate() {
        return null;
    }

    @Override
    public CachedSymbolCloseDates apply(SymbolCloseDates closeDates) {
        return cache;
    }
}
