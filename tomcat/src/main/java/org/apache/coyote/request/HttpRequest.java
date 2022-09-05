package org.apache.coyote.request;

import java.util.Map;

public class HttpRequest {

    private static final String HTML_EXTENSION = ".html";
    private static final String QUERY_START_CHARACTER = "?";
    private static final String ROOT = "/";
    private static final String EXTENSION_CHARACTER = ".";
    private static final String DEFAULT_PAGE_URL = "/index.html";

    private final StartLine startLine;
    private final Map<String, String> headers;
    private final String requestBody;

    public HttpRequest(final String startLine, final Map<String, String> headers,final String requestBody) {
        this.startLine = StartLine.from(startLine);
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public String getRequestUrlWithoutQuery() {
        final String requestUrl = getRequestUrl();
        if (requestUrl.contains(QUERY_START_CHARACTER)) {
            final int index = requestUrl.indexOf(QUERY_START_CHARACTER);
            return requestUrl.substring(0, index);
        }
        return requestUrl;
    }

    public String getRequestUrl() {
        String requestUrl = startLine.getUri();
        requestUrl = makeDefaultRequestUrl(requestUrl);

        return requestUrl;
    }

    private String makeDefaultRequestUrl(String requestUrl) {
        if (requestUrl.equals(ROOT)) {
            return DEFAULT_PAGE_URL;
        }
        if (!requestUrl.contains(EXTENSION_CHARACTER)) {
            return addExtension(requestUrl);
        }
        return requestUrl;
    }

    private String addExtension(final String requestUrl) {
        final int index = requestUrl.indexOf(QUERY_START_CHARACTER);
        if (index != -1) {
            final String path = requestUrl.substring(0, index);
            final String queryString = requestUrl.substring(index + 1);
            return path + HTML_EXTENSION + QUERY_START_CHARACTER + queryString;
        }
        return requestUrl + HTML_EXTENSION;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public HttpMethod getRequestMethod() {
        return startLine.getMethod();
    }
}
