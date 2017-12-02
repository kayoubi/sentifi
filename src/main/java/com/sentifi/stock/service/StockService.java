package com.sentifi.stock.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentifi.stock.exceptions.QuandlException;
import com.sentifi.stock.model.Dma;
import com.sentifi.stock.model.DmaResult;
import com.sentifi.stock.model.Price;
import com.sentifi.stock.model.StockPriceResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author khaled
 */
@Service
public class StockService {
    @Value("${quandlUrl}")
    private String quandlUrl;

    @Value("${apiKey}")
    private String apiKey;

    public StockPriceResult getStocks(final String symbol, final Date startDate, final Date endDate) {
        final Result result = query(symbol, startDate, endDate);
        return new StockPriceResult(Collections.singletonList(new Price(symbol, result.getDataset().getData())));
    }

    public DmaResult get200Dma(final String symbol, final Date startDate) {
        final LocalDateTime endDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(200);
        final Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
        final Result result = query(symbol, startDate, endDate);

        final Double zero = Double.parseDouble("0");
        final Double avg = result.getDataset().getData().stream().mapToDouble(p -> Double.parseDouble(p.get(1).toString())).average().orElse(zero);
        final String oldestAvailableDate = avg.equals(zero) ? result.getDataset().getOldestAvailableDate() : null;
        return new DmaResult(new Dma(symbol, avg.toString(), oldestAvailableDate));
    }

    private Result query(final String symbol, final Date startDate, final Date endDate) {
        final String dateFormat = "yyyy-MM-dd";
        final DateFormat df = new SimpleDateFormat(dateFormat); // need a new instance per thread
        final String url = quandlUrl + symbol + ".json?start_date=" + df.format(startDate) + "&end_date=" + df.format(endDate) + "&column_index=4&api_key=" + apiKey;
        final RestTemplate restTemplate = new RestTemplate();

        try {
            return restTemplate.getForObject(url, Result.class);
        } catch (HttpClientErrorException ex) {
            try {
                final Error error = new ObjectMapper().readValue(ex.getResponseBodyAsString(), Error.class);
                throw new QuandlException(error.getQuandlError().getMessage());
            } catch (IOException io) {
                throw new QuandlException(io.getMessage());
            }
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Result {
        private Dataset dataset;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Dataset {
        @JsonProperty("oldest_available_date")
        private String oldestAvailableDate;
        private List<List<Object>> data;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Error {
        @JsonProperty("quandl_error")
        private QuandlError quandlError;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class QuandlError {
        private String code;
        private String message;
    }
}


