package com.sentifi.stock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author khaled
 */
@Getter
@AllArgsConstructor
public class StockPriceResult {
    @JsonProperty("Prices")
    private final List<Price> prices;
}
