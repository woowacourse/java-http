package com.techcourse.presentation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public abstract class RequestParam<O> {

    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private final Map<String, String> params;

    public RequestParam(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split(PARAM_DELIMITER);
            for (String pair : pairs) {
                String[] keyValue = pair.split(KEY_VALUE_DELIMITER);
                String key = decode(keyValue[0]);
                String value = getParamValue(keyValue);
                params.put(key, value);
            }
        }
        this.params = params;
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8을 디코딩 하는데 실패했어요ㅜ");
        }
    }

    private String getParamValue(String[] keyValue) {
        if (keyValue.length > 1) {
            return decode(keyValue[1]);
        }
        return "";
    }

    public abstract O toObject();

    protected String getParam(String key) {
        return params.getOrDefault(key, "");
    }
}
