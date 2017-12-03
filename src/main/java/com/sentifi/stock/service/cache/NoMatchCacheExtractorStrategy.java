package com.sentifi.stock.service.cache;

import com.sentifi.stock.domain.SymbolCloseDates;

import java.util.Date;

/**
 * A strategy to match two cases
 *  1) request start date is after the end date of the cache
 *  2) request end date is before the start date of the cache
 *
 * In this case we'll request the new data ane replace the cache
 *
 * @author khaled
 */
public class NoMatchCacheExtractorStrategy extends CacheExtractorStrategy {
    @Override
    public boolean _match(CachedSymbolCloseDates cache, Date startDate, Date endDate) {
        return startDate.after(cache.endDate) || endDate.before(cache.startDate);
    }

    @Override
    public Date getStartDate() {
        return originalStartDate;
    }

    @Override
    public Date getEndDate() {
        return originalEndDate;
    }

    @Override
    public CachedSymbolCloseDates apply(SymbolCloseDates closeDates) {
        // will ignore the original cache and populate with a new one
        return new CachedSymbolCloseDates(originalStartDate, originalEndDate, closeDates);
    }
}
