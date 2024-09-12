package org.apache.coyote.http11.protocol.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParameters {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int VALID_KEY_VALUE_LENGTH = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> parameters;

    public QueryParameters(String query) {
        this.parameters = parseQuery(query);
    }

    private Map<String, String> parseQuery(String query) {
        return Arrays.stream(query.split(PARAMETER_DELIMITER))
                .filter(param -> param.contains(KEY_VALUE_DELIMITER))
                .map(param -> param.split(KEY_VALUE_DELIMITER))
                .filter(param -> param.length == VALID_KEY_VALUE_LENGTH)
                .filter(param -> !param[KEY_INDEX].isEmpty())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(param -> param[KEY_INDEX], param -> param[VALUE_INDEX]),
                        HashMap::new)
                );
    }

    public String get(String key) {
        return parameters.get(key);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
