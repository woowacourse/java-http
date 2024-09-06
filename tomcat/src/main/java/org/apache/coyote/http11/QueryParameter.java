package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QueryParameter {

    private static final String PAIR_DELIMITER = "=";
    private static final String PARAMETER_DELIMITER = "&";

    private final Map<String, String> queryParameter = new HashMap<>();

    public QueryParameter(String queryParameter) {
        if (queryParameter == null) {
            queryParameter = "";
        }

        parseQueryParameter(queryParameter);
    }

    private void parseQueryParameter(String queryParameter) {
        String[] pairs = queryParameter.split(PARAMETER_DELIMITER);

        for (String pair : pairs) {
            if (pair.contains(PAIR_DELIMITER)) {
                String[] keyValuePair = pair.split(PAIR_DELIMITER);
                putIfValidPair(keyValuePair);
            }
        }
    }

    private void putIfValidPair(String[] keyValuePair) {
        if (keyValuePair.length != 2) {
            return;
        }
        String key = keyValuePair[0];
        String value = keyValuePair[1];

        queryParameter.put(key, value);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(queryParameter.get(key));
    }

    public boolean isEmpty() {
        return queryParameter.isEmpty();
    }

    public int getSize() {
        return queryParameter.size();
    }
}
