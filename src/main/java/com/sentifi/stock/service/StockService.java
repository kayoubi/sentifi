package com.sentifi.stock.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentifi.stock.exceptions.QuandlException;
import com.sentifi.stock.model.Price;
import com.sentifi.stock.model.StockPriceResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author khaled
 */
@Service
public class StockService {
    @Value("${quandlUrl}")
    private String quandlUrl;

    @Value("${apiKey}")
    private String apiKey;

    private final String dateFormat = "yyyy-MM-dd";

    public StockPriceResult getStocks(String symbol, Date startDate, Date endDate) {
        final DateFormat df = new SimpleDateFormat(dateFormat); // need a new instance per thread
        String url = quandlUrl + symbol + ".json?start_date=" + df.format(startDate) + "&end_date=" + df.format(endDate) + "&column_index=4&api_key=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();

        try {
            Result result = restTemplate.getForObject(url, Result.class);
            return new StockPriceResult(Collections.singletonList(new Price(symbol, result.getDataset().getData())));
        } catch (HttpClientErrorException ex) {
            try {
                Error error = new ObjectMapper().readValue(ex.getResponseBodyAsString(), Error.class);
                throw new QuandlException(error.getQuandlError().getMessage());
            } catch (IOException io) {
                throw new QuandlException(io.getMessage());
            }
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private Dataset dataset;

        public Result() {
        }

        public Dataset getDataset() {
            return dataset;
        }

        public void setDataset(Dataset dataset) {
            this.dataset = dataset;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Dataset {
        private List<List<Object>> data;

        public Dataset() {
        }

        List<List<Object>> getData() {
            return data;
        }

        public void setData(List<List<Object>> data) {
            this.data = data;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {
        @JsonProperty("quandl_error")
        private QuandlError quandlError;

        public QuandlError getQuandlError() {
            return quandlError;
        }

        public void setQuandlError(QuandlError quandlError) {
            this.quandlError = quandlError;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QuandlError {
        private String code;
        private String message;

        public QuandlError() {
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}


