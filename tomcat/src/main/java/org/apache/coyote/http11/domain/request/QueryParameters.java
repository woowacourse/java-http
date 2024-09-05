package org.apache.coyote.http11.domain.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParameters {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> parameters;

    public QueryParameters(String query) {
        this.parameters = parseQuery(query);
    }

    private Map<String, String> parseQuery(String query) {
        return Arrays.stream(query.split(PARAMETER_DELIMITER))
                .filter(param -> param.contains(KEY_VALUE_DELIMITER))
                .map(param -> param.split(KEY_VALUE_DELIMITER))
                .filter(param -> param.length == 2)
                .filter(param -> !param[0].isEmpty())
                .collect(Collectors.collectingAndThen(Collectors.toMap(param -> param[0], param -> param[1]),
                        HashMap::new));
    }

    public String get(String key) {
        return parameters.get(key);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
