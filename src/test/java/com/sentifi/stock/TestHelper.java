package com.sentifi.stock;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author khaled
 */
public class TestHelper {
    public static Date getDate(String st) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(st);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
