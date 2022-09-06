package org.apache.coyote.http.request;

import java.util.Map;

public class RequestPath {

    private static final String QUERY_IDENTIFIER = "?";
    private static final String HTML_EXTENSION = ".html";
    private static final String BLANK_QUERY_STRING = "";

    private final String path;
    private final QueryParams queryParams;

    public RequestPath(final String path, final QueryParams queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static RequestPath from(final String url) {
        return new RequestPath(extractPath(url), QueryParams.from(extractQueryString(url)));
    }

    private static String extractPath(final String url) {
        if (url.contains(QUERY_IDENTIFIER) && !url.contains(HTML_EXTENSION)) {
            int index = url.indexOf(QUERY_IDENTIFIER);
            return url.substring(0, index) + HTML_EXTENSION;
        }
        if (url.equals("/")) {
            return url;
        }
        if (!url.contains(".")) {
            return url + HTML_EXTENSION;
        }
        return url;
    }

    private static String extractQueryString(final String url) {
        if (url.contains(QUERY_IDENTIFIER)) {
            int index = url.indexOf(QUERY_IDENTIFIER);
            return url.substring(index + 1);
        }
        return BLANK_QUERY_STRING;
    }

    public boolean hasQueryParams() {
        return queryParams.isNotEmpty();
    }

    public Map<String, String> getQueryParams() {
        return queryParams.getParams();
    }

    public String getPath() {
        return path;
    }
}
