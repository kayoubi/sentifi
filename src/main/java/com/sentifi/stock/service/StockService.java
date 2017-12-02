package com.sentifi.stock.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sentifi.stock.model.Price;
import com.sentifi.stock.model.StockPriceResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

        Result result = restTemplate.getForObject(url, Result.class);
        return new StockPriceResult(Collections.singletonList(new Price(symbol, result.getDataset().getData())));
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
}


