package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.Map;

public class RequestURL {

    private static final String QUERY_PARAM_REGEX = "?";

    private final String path;
    private final Map<String, String> queryParams;

    private RequestURL(final String path, final Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static RequestURL from(final String requestURL) {
        if (requestURL.contains(QUERY_PARAM_REGEX)) {
            final int index = requestURL.indexOf(QUERY_PARAM_REGEX);
            final String path = requestURL.substring(0, index);
            final String queryString = requestURL.substring(index + 1);
            final Map<String, String> params = QueryParser.parsingQueryString(queryString);
            return new RequestURL(path, params);
        }
        return new RequestURL(requestURL, Collections.emptyMap());
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }
}
