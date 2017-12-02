package com.sentifi.stock.service.cache;

import com.sentifi.stock.domain.SymbolCloseDates;

import java.util.Date;

/**
 * @author khaled
 */
public abstract class CacheExtractorStrategy {
    CachedSymbolCloseDates cache;
    Date originalStartDate;
    Date originalEndDate;

    public boolean match(CachedSymbolCloseDates cache, Date startDate, Date endDate) {
        this.cache = cache;
        this.originalStartDate = startDate;
        this.originalEndDate = endDate;

        return _match(cache, startDate, endDate);
    }

    public boolean needExtraData() {
        return getStartDate() != null && getEndDate() != null;
    }

    public abstract boolean _match(CachedSymbolCloseDates cache, Date startDate, Date endDate);

    public abstract Date getStartDate();

    public abstract Date getEndDate();

    public abstract CachedSymbolCloseDates apply(SymbolCloseDates closeDates);
}
