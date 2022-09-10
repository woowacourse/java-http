package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final int DEFAULT_QUERY_PARAMETER_PAIR_SIZE = 2;

    private final String contents;

    public RequestBody(final String contents) {
        this.contents = contents;
    }

    public Map<String, String> parseApplicationFormData() {
        final Map<String, String> params = new HashMap<>();
        final String[] paramPairs = contents.split("&");

        for (final String param : paramPairs) {
            addParamPair(params, param);
        }

        return params;
    }

    private void addParamPair(final Map<String, String> params, final String param) {
        if (param == null || param.isBlank()) {
            return;
        }

        final String[] pair = param.split("=");
        if (pair.length != DEFAULT_QUERY_PARAMETER_PAIR_SIZE) {
            throw new IllegalArgumentException("올바른 application/x-www-form-urlencoded 형식이 아닙니다.");
        }
        params.put(pair[0], pair[1]);
    }
}
