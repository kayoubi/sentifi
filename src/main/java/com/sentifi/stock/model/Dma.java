package com.sentifi.stock.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author khaled
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dma {
    @JsonProperty("Ticker")
    private final String ticker;
    @JsonProperty("Avg")
    private final String avg;
    @JsonProperty("OldestAvailableDate")
    private final String oldestAvailableDate;
}
