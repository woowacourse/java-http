package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParam {

    private static String PARAM_DELIMITER = "&";
    private static String VALUE_DELIMITER = "=";

    private final Map<String, String> params;

    public QueryParam(String queryString) {
        params = Arrays.stream(queryString.split(PARAM_DELIMITER))
                .map(params -> params.split(VALUE_DELIMITER))
                .filter(tokens -> tokens.length == 2)
                .collect(Collectors.toMap(
                        tokens -> tokens[0],
                        tokens -> tokens[1]));
    }

    public String getValue(String name) {
        return params.getOrDefault(name, "");
    }
}
