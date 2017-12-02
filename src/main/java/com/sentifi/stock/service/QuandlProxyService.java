package com.sentifi.stock.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentifi.stock.domain.SymbolCloseDates;
import com.sentifi.stock.exceptions.QuandlException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author khaled
 */
@Service
public class QuandlProxyService {
    @Value("${quandlUrl}")
    private String quandlUrl;
    @Value("${apiKey}")
    private String apiKey;

    SymbolCloseDates query(final String symbol, final Date startDate, final Date endDate) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // need a new instance per thread
        final String url = quandlUrl.concat(symbol).concat(".json?start_date=").concat(df.format(startDate)).concat("&end_date=").concat(df.format(endDate)).concat("&column_index=4&api_key=").concat(apiKey);
        final RestTemplate restTemplate = new RestTemplate();

        try {
            Result result = restTemplate.getForObject(url, Result.class);
            return new SymbolCloseDates(
                symbol,
                result.getDataset().getData().stream().map(d -> new SymbolCloseDates.CloseDate(d.get(0).toString(), d.get(1).toString())).collect(Collectors.toList()),
                result.getDataset().getOldestAvailableDate()
            );
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
