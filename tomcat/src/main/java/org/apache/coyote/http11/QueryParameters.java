package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryParameters {

    private static final String PAIR_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int VALID_PAIR_COUNT = 2;

    private final Map<String, String> parameterInfo;

    public QueryParameters() {
        this.parameterInfo = Collections.emptyMap();
    }

    public QueryParameters(String queryString) {
        this.parameterInfo = new HashMap<>();
        String[] pairs = queryString.split(PAIR_DELIMITER);

        for (String pair : pairs) {
            String[] keyAndValue = pair.split(KEY_VALUE_DELIMITER, VALID_PAIR_COUNT);

            if (keyAndValue.length != VALID_PAIR_COUNT) {
                throw new IllegalArgumentException("Invalid query string: " + queryString);
            }
            parameterInfo.put(keyAndValue[0], keyAndValue[1]);
        }
    }

    public String getParameter(String key) {
        return parameterInfo.get(key);
    }
}
