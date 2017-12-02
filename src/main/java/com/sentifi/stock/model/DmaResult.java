package com.sentifi.stock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author khaled
 */
@Getter
@AllArgsConstructor
public class DmaResult {
    @JsonProperty("200dma")
    private final Dma dma;
}
