package com.sentifi.stock.service.cache;

import com.sentifi.stock.domain.SymbolCloseDates;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sentifi.stock.util.DateUtil.addDays;
import static com.sentifi.stock.util.DateUtil.isBetween;

/**
 * @author khaled
 */
@Service
public class LeftOverlapCacheExtractorStrategy extends CacheExtractorStrategy {
    @Override
    public boolean _match(CachedSymbolCloseDates cache, Date startDate, Date endDate) {
        return startDate.before(cache.startDate) && isBetween(endDate, cache.startDate, cache.endDate);
    }

    @Override
    public Date getStartDate() {
        return originalStartDate;
    }

    @Override
    public Date getEndDate() {
        return addDays(cache.startDate, -1);
    }

    @Override
    public CachedSymbolCloseDates apply(SymbolCloseDates closeDates) {
        return new CachedSymbolCloseDates(
            originalStartDate,
            cache.endDate,
            new SymbolCloseDates(
                closeDates.getSymbol(),
                Stream.concat(cache.getCache().getCloseDates().stream(), closeDates.getCloseDates().stream()).collect(Collectors.toList()),
                closeDates.getOldestAvailableDate())
        );
    }
}
