package org.apache.coyote.http11.model;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class QueryParameter {

    private static final String PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int VALID_PAIR_COUNT = 2;

    private final Map<String, String> queryParameter;

    public QueryParameter() {
        this.queryParameter = new HashMap<>();
    }

    public QueryParameter(String queryString) {
        this.queryParameter = new HashMap<>();
        doParse(queryString);
    }

    public String getParameter(String key) {
        return queryParameter.get(key);
    }

    public void addParameterFromBody(String body) {
        doParse(body);
    }

    private void doParse(String data) {
        if (data == null || data.isBlank()) {
            return;
        }
        String[] pairs = data.split(PAIR_DELIMITER);
        for (String pair : pairs) {
            String[] keyValue = pair.split(KEY_VALUE_DELIMITER, VALID_PAIR_COUNT);
            if (keyValue[0].isBlank()) {
                continue;
            }
            try {
                String key = URLDecoder.decode(keyValue[0], "UTF-8");
                String value = "";
                if(keyValue.length > 1) {
                    value = URLDecoder.decode(keyValue[1], "UTF-8");
                }
                queryParameter.put(key, value);
            } catch (java.io.UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Failed to decode parameter: " + pair, e);
            }
        }
    }
}
