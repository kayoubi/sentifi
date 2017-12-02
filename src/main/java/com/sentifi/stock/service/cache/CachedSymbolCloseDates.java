package com.sentifi.stock.service.cache;

import com.sentifi.stock.domain.SymbolCloseDates;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

/**
 * @author khaled
 */
@Getter
@AllArgsConstructor
public class CachedSymbolCloseDates {
    final Date startDate;
    final Date endDate;
    final SymbolCloseDates cache;
}
