package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpRequestQueryString {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> parameters;

    public HttpRequestQueryString(final String queryString) {
        parameters = parseQueryString(queryString);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        if (Objects.isNull(queryString) || queryString.isEmpty()) {
            return Collections.emptyMap();
        }

        final String[] keyValuePairs = queryString.split(PARAMETER_DELIMITER);

        return Arrays.stream(keyValuePairs)
            .map(keyValuePair -> keyValuePair.split(KEY_VALUE_DELIMITER))
            .filter(keyValue -> keyValue.length == 2)
            .collect(Collectors.toMap(
                keyValue -> keyValue[0],
                keyValue -> keyValue[1],
                (prev, update) -> update
            ));
    }

    public boolean contains(final String parmeterKey) {
        return parameters.containsKey(parmeterKey);
    }

    public String getValue(final String parameterKey) {
        if (parameters.containsKey(parameterKey)) {
            return parameters.get(parameterKey);
        }
        return null;
    }

    public Map<String, String> getParameters() {
        return new HashMap<>(parameters);
    }
}
