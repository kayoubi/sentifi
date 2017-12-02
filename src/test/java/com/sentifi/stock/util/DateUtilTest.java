package com.sentifi.stock.util;

import org.junit.Test;

import java.util.Date;

import static com.sentifi.stock.TestHelper.getDate;
import static com.sentifi.stock.util.DateUtil.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author khaled
 */
public class DateUtilTest {

    @Test
    public void testIsBefore() {
        Date d = getDate("2012-10-15");

        assertTrue(isBefore(getDate("2012-09-15"), d));
        assertTrue(isBefore(getDate("2012-10-15"), d));
        assertFalse(isBefore(getDate("2012-10-18"), d));
    }

    @Test
    public void testIsAfter() {
        Date d = getDate("2012-10-15");

        assertTrue(isAfter(getDate("2012-12-15"), d));
        assertTrue(isAfter(getDate("2012-10-15"), d));
        assertFalse(isAfter(getDate("2012-09-15"), d));
    }

    @Test
    public void testIsBetween() {
        Date d1 = getDate("2012-10-10");
        Date d2 = getDate("2012-10-15");

        assertTrue(isBetween(getDate("2012-10-12"), d1, d2));
        assertTrue(isBetween(getDate("2012-10-10"), d1, d2));
        assertTrue(isBetween(getDate("2012-10-15"), d1, d2));
        assertFalse(isBetween(getDate("2012-09-12"), d1, d2));
    }
}