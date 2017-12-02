package com.sentifi.stock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sentifi.stock.domain.SymbolCloseDates;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author khaled
 */
@Getter
public class Price {
    @JsonProperty("Ticker")
    private final String ticker;
    @JsonProperty("DateClose")
    private final List<List<String>> dateClose;

    public Price(String ticker, List<SymbolCloseDates.CloseDate> dateClose) {
        this.ticker = ticker;
        this.dateClose = dateClose.stream().map(l -> Arrays.asList(l.getDate(), l.getClose())).collect(Collectors.toList());
    }
}
