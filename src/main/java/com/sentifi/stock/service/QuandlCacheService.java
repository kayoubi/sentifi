package com.sentifi.stock.service;

import com.sentifi.stock.domain.SymbolCloseDates;
import com.sentifi.stock.service.cache.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.sentifi.stock.util.DateUtil.isBetween;

/**
 * @author khaled
 */
@Service
public class QuandlCacheService {
    final Map<String, CachedSymbolCloseDates> cache;
    final QuandlProxyService quandlProxyService;

    public QuandlCacheService(QuandlProxyService quandlProxyService) {
        this.quandlProxyService = quandlProxyService;
        this.cache = new LinkedHashMap<String, CachedSymbolCloseDates>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CachedSymbolCloseDates> eldest) {
                return size() > 100000;
            }
        };
    }

    SymbolCloseDates query(final String symbol, final Date startDate, final Date endDate) {
        populateCache(symbol, startDate, endDate);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SymbolCloseDates cached = cache.get(symbol).getCache();
        return new SymbolCloseDates(
            symbol,
            cached.getCloseDates()
                .stream()
                .filter(closeDate -> isBetween(getDate(df, closeDate.getDate()), startDate, endDate))
                .collect(Collectors.toList()),
            cached.getOldestAvailableDate());
    }

    private void populateCache(final String symbol, final Date startDate, final Date endDate) {
        cache.compute(
            symbol,
            (s, cachedSymbolCloseDates) -> {
                if (cachedSymbolCloseDates == null) {
                    return new CachedSymbolCloseDates(startDate, endDate, quandlProxyService.query(symbol, startDate, endDate));
                }
                final CacheExtractorStrategy strategy = strategies().stream()
                    .filter(cacheExtractorStrategy -> cacheExtractorStrategy.match(cachedSymbolCloseDates, startDate, endDate))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Internal Error"));
                return strategy.apply(strategy.needExtraData() ? quandlProxyService.query(symbol, strategy.getStartDate(), strategy.getEndDate()) : null);
            });
    }

    private List<CacheExtractorStrategy> strategies() {
        return Arrays.asList(
            new FullOverlapCacheExtractorStrategy(),
            new InnerOverlapCacheExtractorStrategy(),
            new LeftOverlapCacheExtractorStrategy(),
            new NoMatchCacheExtractorStrategy(),
            new RightOverlapCacheExtractorStrategy()
        );
    }

    private Date getDate(final SimpleDateFormat dateFormat, final String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Internal Error");
        }
    }
}


