package org.apache.coyote.request;

import java.util.Map;

public class HttpRequestHeader {

    private static final String HTML_EXTENSION = ".html";
    private static final String QUERY_START_CHARACTER = "?";
    private static final String ROOT = "/";
    private static final String EXTENSION_CHARACTER = ".";
    private static final String DEFAULT_PAGE_URL = "/index.html";
    private static final String START_LINE_REGEX = " ";
    private static final int URL_INDEX = 1;

    private final String startLine;
    private final Map<String, String> headers;

    public HttpRequestHeader(final String startLine, final Map<String, String> headers) {
        this.startLine = startLine;
        this.headers = headers;
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
        String requestUrl = startLine.split(START_LINE_REGEX)[URL_INDEX];
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
}
