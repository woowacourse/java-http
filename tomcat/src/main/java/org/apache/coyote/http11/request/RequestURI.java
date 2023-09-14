package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestURI {

    private static final char QUERY_STRING_DELIMITER = '?';

    private final String path;
    private final HttpRequestQueryString queryString;

    public RequestURI(final String path, final HttpRequestQueryString queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static RequestURI from(final String requestTarget) {
        return new RequestURI(extractPath(requestTarget), extractQueryString(requestTarget));
    }

    private static String extractPath(final String requestTarget) {
        final int delimiterIndex = requestTarget.indexOf(QUERY_STRING_DELIMITER);
        if (delimiterIndex != -1) {
            return requestTarget.substring(0, delimiterIndex);
        }
        return requestTarget;
    }

    private static HttpRequestQueryString extractQueryString(final String targetUrl) {
        final int delimiterIndex = targetUrl.indexOf(QUERY_STRING_DELIMITER);
        if (delimiterIndex != -1) {
            return HttpRequestQueryString.from(targetUrl.substring(delimiterIndex + 1));
        }
        return HttpRequestQueryString.emtpy();
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParameters() {
        return queryString.getParameters();
    }

    @Override
    public String toString() {
        return "RequestTarget{" +
            "path='" + path + '\'' +
            ", queryString=" + queryString +
            '}';
    }
}
