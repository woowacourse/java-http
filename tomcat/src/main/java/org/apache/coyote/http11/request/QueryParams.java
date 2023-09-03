package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import org.apache.coyote.http11.util.QueryParser;

public class QueryParams {

    private final Map<String, String> params;

    public QueryParams(final Map<String, String> params) {
        this.params = params;
    }

    public static QueryParams create(String line) {
        int questionMarkIdx = line.indexOf("?");
        if (questionMarkIdx == -1) {
            return QueryParams.empty();
        }
        String paramLine = line.substring(questionMarkIdx + 1);
        return QueryParser.parse(paramLine);
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
