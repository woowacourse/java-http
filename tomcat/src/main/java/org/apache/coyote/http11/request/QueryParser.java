package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QueryParser {

    private static final int DEFAULT_QUERY_PARAMETER_PAIR_SIZE = 2;

    private QueryParser() {
    }

    static Map<String, String> parse(final String contents, final String errorMessage) {
        final Map<String, String> params = new HashMap<>();
        final String[] paramPairs = contents.split("&");

        for (final String param : paramPairs) {
            addParamPair(params, param, errorMessage);
        }
        return params;
    }

    static void addParamPair(final Map<String, String> params, final String param, final String errorMessage) {
        if (Objects.isNull(param) || param.isBlank()) {
            return;
        }

        final String[] pair = param.split("=");
        if (pair.length != DEFAULT_QUERY_PARAMETER_PAIR_SIZE) {
            throw new IllegalArgumentException(errorMessage);
        }
        params.put(pair[0], pair[1]);
    }
}
