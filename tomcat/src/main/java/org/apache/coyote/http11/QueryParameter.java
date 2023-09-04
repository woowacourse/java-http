package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryParameter {
    public static QueryParameter EMPTY = new QueryParameter(new HashMap<>());
    private Map<String, String> queryParameter = new HashMap<>();

    public QueryParameter(Map<String, String> queryParameter) {
        this.queryParameter = queryParameter;
    }

    public QueryParameter(final String uri) {
        final int index = uri.indexOf("?");
        final String queryString = uri.substring(index + 1);
        final String[] splitedQueryStrings = queryString.split("&"); // account=gugu
        for (final String str : splitedQueryStrings) {
            final int strIndex = str.indexOf("=");
            queryParameter.put(str.substring(0, strIndex), str.substring(strIndex + 1));
        }
    }

    public Map<String, String> getQueryParameter() {
        return queryParameter;
    }
}
