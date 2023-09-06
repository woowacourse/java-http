package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private static final SessionManager sessionManager = new SessionManager();

    private final HttpRequestLine httpRequestLine;
    private final Map<String, String> headers;
    private final Map<String, String> cookies;
    private final String body;

    public HttpRequest(HttpRequestLine httpRequestLine, Map<String, String> headers, Map<String, String> cookies,
                       String body) {
        this.httpRequestLine = httpRequestLine;
        this.headers = headers;
        this.cookies = cookies;
        this.body = body;
    }

    public String getUri() {
        String target = httpRequestLine.getPath();
        int queryStringIdx = target.indexOf("?");
        if (queryStringIdx == -1) {
            return target;
        }

        return target.substring(0, queryStringIdx);
    }

    public Map<String, String> getQueryString() {
        String target = httpRequestLine.getPath();
        int queryStringIdx = target.indexOf("?");
        if (queryStringIdx == -1) {
            throw new IllegalStateException();
        }

        String queryStrings = target.substring(queryStringIdx + 1);
        return parseKeyAndValue(queryStrings);
    }

    public String getBody() {
        return body;
    }

    private Map<String, String> parseKeyAndValue(String input) {
        return Arrays.stream(input.split("&"))
            .map(it -> it.split("="))
            .collect(Collectors.toMap(
                keyAndValue -> keyAndValue[0],
                keyAndValue -> keyAndValue[1]));
    }

    public HttpMethod getMethod() {
        return httpRequestLine.getMethod();
    }

    public Map<String, String> getCookie() {
        return cookies;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
