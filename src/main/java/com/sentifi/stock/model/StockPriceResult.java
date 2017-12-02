package com.sentifi.stock.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author khaled
 */
public class StockPriceResult {
    @JsonProperty("Prices")
    private final List<Price> prices;

    public StockPriceResult(List<Price> prices) {
        this.prices = prices;
    }

    public List<Price> getPrices() {
        return prices;
    }
}
