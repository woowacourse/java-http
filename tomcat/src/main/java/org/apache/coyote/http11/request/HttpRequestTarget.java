package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequestTarget {

    private static final char QUERY_STRING_DELIMITER = '?';
    private static final String EMPTY_QUERY_STRING = "";
    
    private final String path;
    private final HttpRequestQueryString queryString;

    public HttpRequestTarget(final String targetUrl) {
        this.path = extractPath(targetUrl);
        this.queryString = new HttpRequestQueryString(extractQueryString(targetUrl));
    }

    private String extractPath(final String targetUrl) {
        final int delimiterIndex = targetUrl.indexOf(QUERY_STRING_DELIMITER);
        if (delimiterIndex != -1) {
            return targetUrl.substring(0, delimiterIndex);
        }
        return targetUrl;
    }

    private String extractQueryString(final String targetUrl) {
        final int delimiterIndex = targetUrl.indexOf(QUERY_STRING_DELIMITER);
        if (delimiterIndex != -1) {
            return targetUrl.substring(delimiterIndex + 1);
        }
        return EMPTY_QUERY_STRING;
    }

    public String getPath() {
        return path;
    }

    public boolean containsParameter(final String parameterKey) {
        return queryString.contains(parameterKey);
    }

    public String getParameterValue(final String parameterKey) {
        return queryString.getValue(parameterKey);
    }

    public Map<String, String> getParameters() {
        return queryString.getParameters();
    }
}
