package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> queryStrings;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(final HttpMethod httpMethod, final String path, final Map<String, String> queryStrings,
                        final String httpVersion,
                        final Map<String, String> headers, final String body) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryStrings = queryStrings;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(final String startLine, final Map<String, String> headers, final String body) {
        final String[] split = startLine.split(" ");
        final String uri = split[1];
        String path = uri;
        String queryString = null;
        if(uri.contains("?")){
            int index = uri.indexOf("?");
            path = uri.substring(0, index);
            queryString = uri.substring(index + 1);
        }
        return new HttpRequest(
                HttpMethod.from(split[0]),
                path,
                parseQueryString(queryString),
                split[2],
                headers,
                body
        );
    }

    private static Map<String, String> parseQueryString(final String string) {
        Map<String, String> queryStrings = new HashMap<>();
        if(string == null) {
            return queryStrings;
        }
        final String[] split = string.split("&");
        for (String queryString : split) {
            final String[] keyAndValue = queryString.split("=");
            queryStrings.put(keyAndValue[0], keyAndValue[1]);
        }
        return queryStrings;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryStrings() {
        return queryStrings;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
