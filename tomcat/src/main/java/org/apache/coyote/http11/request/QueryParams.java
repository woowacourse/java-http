package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QueryParams {

    private final Map<String, String> params;

    private QueryParams(final Map<String, String> params) {
        this.params = params;
    }

    public static QueryParams create(String line) {
        int questionMarkIdx = line.indexOf("?");
        if (questionMarkIdx == -1) {
            return QueryParams.empty();
        }
        String paramLine = line.substring(questionMarkIdx + 1);
        String[] split = paramLine.split("&");

        Map<String, String> params = new HashMap<>();
        for (String piece : split) {
            int equalSignIdx = piece.indexOf("=");
            params.put(piece.substring(0, equalSignIdx), piece.substring(equalSignIdx+1));
        }

        return new QueryParams(params);
    }

    private static QueryParams empty() {
        return new QueryParams(null);
    }

    public boolean hasParams(String... params) {
        if (this.params == null) {
            return false;
        }
        return Arrays.stream(params)
                .filter(it -> this.params.get(it) == null)
                .findAny()
                .isEmpty();
    }

    public String getParam(String name) {
        return params.get(name);
    }
}
