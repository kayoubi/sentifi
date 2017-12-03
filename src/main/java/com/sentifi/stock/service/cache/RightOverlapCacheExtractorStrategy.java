package com.sentifi.stock.service.cache;

import com.sentifi.stock.domain.SymbolCloseDates;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sentifi.stock.util.DateUtil.addDays;
import static com.sentifi.stock.util.DateUtil.isBetween;

/**
 * A strategy to match the case where the request date range overlap with cache from the right side (request start date in the cache)
 *
 * in this case we query only the date range from (cache end date + 1 -> request end date) and add that to beginning of the cache
 *
 * @author khaled
 */
public class RightOverlapCacheExtractorStrategy extends CacheExtractorStrategy {
    @Override
    public boolean _match(CachedSymbolCloseDates cache, Date startDate, Date endDate) {
        return isBetween(startDate, cache.startDate, cache.endDate) && endDate.after(cache.endDate);
    }

    @Override
    public Date getStartDate() {
        return addDays(cache.endDate, 1);
    }

    @Override
    public Date getEndDate() {
        return originalEndDate;
    }

    @Override
    public CachedSymbolCloseDates apply(SymbolCloseDates closeDates) {
        return new CachedSymbolCloseDates(
            cache.startDate,
            originalEndDate,
            new SymbolCloseDates(
                closeDates.getSymbol(),
                Stream.concat(closeDates.getCloseDates().stream(), cache.getCache().getCloseDates().stream()).collect(Collectors.toList()),
                closeDates.getOldestAvailableDate())
        );
    }
}
