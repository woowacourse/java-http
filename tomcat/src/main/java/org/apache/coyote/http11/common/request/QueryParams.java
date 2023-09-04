package org.apache.coyote.http11.common.request;

import java.util.Map;
import org.apache.coyote.http11.util.Parser;

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
        return Parser.parseToQueryParams(paramLine);
    }

    public static QueryParams empty() {
        return new QueryParams(null);
    }

    public String getParam(String name) {
        return params.get(name);
    }
}
