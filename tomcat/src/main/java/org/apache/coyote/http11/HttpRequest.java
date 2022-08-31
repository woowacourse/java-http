package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String httpRequest;
    private final Map<String, String> headers;

    public static HttpRequest from(final String httpRequest) {
        return new HttpRequest(httpRequest, headers(httpRequest));
    }

    private static Map<String, String> headers(final String httpRequest) {
        final Map<String, String> headers = new HashMap<>();
        final String[] requestLines = httpRequest.split("\n");

        for (int i = 1; i < requestLines.length; i++) {
            if (requestLines[i].isBlank()) {
                break;
            }

            final String name = headerName(requestLines[i]);
            final String value = headerValue(requestLines[i]);
            headers.put(name, value);
        }

        return headers;
    }

    private static String headerName(final String line) {
        return line.trim().split(" ")[0].replaceAll(":", "");
    }

    private static String headerValue(final String line) {
        return line.trim().split(" ")[1];
    }

    private HttpRequest(final String httpRequest, final Map<String, String> headers) {
        this.httpRequest = httpRequest;
        this.headers = headers;
    }

    public boolean hasHeader(String name) {
        return headers.containsKey(name);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getUri() {
        return requestStartLine().split(" ")[1];
    }

    private String requestStartLine() {
        return httpRequest.split("\n")[0];
    }

    public String getRequestParam(String paramName) {
        final String queryString = getQueryString();

        if (queryString.isBlank()) {
            return null;
        }

        for (String param : queryString.split("&")) {
            final String key = param.split("=")[0];
            final String value = param.split("=")[1];

            if (key.equals(paramName)) {
                return value;
            }
        }

        return null;
    }

    private String getQueryString() {
        final int index = getUri().indexOf("?");
        return index == -1 ? "" : getUri().substring(index + 1);
    }
}
