package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryParameters {

    private static final String PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int VALID_PAIR_COUNT = 2;

    private final Map<String, String> parameterInfo;

    public QueryParameters() {
        this.parameterInfo = new HashMap<>();
    }

    public QueryParameters(String queryString) {
        this.parameterInfo = new HashMap<>();
        doParse(queryString);
    }

    public String getParameter(String key) {
        return parameterInfo.get(key);
    }

    public boolean hasAnyParameter() {
        return !parameterInfo.isEmpty();
    }

    public void addParametersFromBody(String body) {
        doParse(body);
    }

    private void doParse(String data) {
        if (data == null || data.isBlank()) {
            return;
        }
        String[] pairs = data.split(PAIR_DELIMITER);
        for (String pair : pairs) {
            String[] keyValue = pair.split(KEY_VALUE_DELIMITER, 2);
            if (keyValue[0].isBlank()) {
                continue;
            }
            try {
                String key = URLDecoder.decode(keyValue[0], "UTF-8");
                String value = "";
                if(keyValue.length > 1) {
                    value = URLDecoder.decode(keyValue[1], "UTF-8");
                }
                parameterInfo.put(key, value);
            } catch (java.io.UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Failed to decode parameter: " + pair, e);
            }
        }
    }
}
