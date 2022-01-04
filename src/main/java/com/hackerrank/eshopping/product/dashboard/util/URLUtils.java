package com.hackerrank.eshopping.product.dashboard.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class URLUtils {

    private final static String UTF8 = "UTF-8";

    public static String decodeString(String stringToDecode) {
        try {
            return URLDecoder.decode(stringToDecode, UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
