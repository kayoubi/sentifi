package com.sentifi.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author khaled
 */
@Getter
@AllArgsConstructor
public class SymbolCloseDates {
    private final String symbol;
    private final List<CloseDate> closeDates;
    private final String oldestAvailableDate;

    @Getter
    @AllArgsConstructor
    public static class CloseDate {
        private final String date;
        private final String close;
    }
}
