package com.sentifi.stock.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author khaled
 */
public class Price {
    @JsonProperty("Ticker")
    private final String ticker;

    @JsonProperty("DateClose")
    private final List<List<String>> dateClose;

    public Price(String ticker, List<List<Object>> dateClose) {
        this.ticker = ticker;
        this.dateClose = dateClose.stream().map(l -> l.stream().map(Object::toString).collect(Collectors.toList())).collect(Collectors.toList());
    }

    public String getTicker() {
        return ticker;
    }

    public List<List<String>> getDateClose() {
        return dateClose;
    }
}
