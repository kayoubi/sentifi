package com.sentifi.stock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

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

    public Price(String ticker, List<List<Object>> dateClose) {
        this.ticker = ticker;
        this.dateClose = dateClose.stream().map(l -> l.stream().map(Object::toString).collect(Collectors.toList())).collect(Collectors.toList());
    }
}
