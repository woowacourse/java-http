package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private String body;

    public HttpRequest(String rawRequestLine, HttpHeaders headers, String body) {
        this.requestLine = new RequestLine(rawRequestLine);
        this.headers = headers;
        this.body = body;
    }

    public boolean isGetMethod() {
        return requestLine.getMethod().isGet();
    }

    public boolean isPostMethod() {
        return requestLine.getMethod().isPost();
    }

    public Optional<String> findCookieByName(String name) {
        return headers.findCookieByName(name);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getPathWithoutQueryString() {
        return getPath().split("\\?")[0];
    }

    public Map<String, String> getQueryString() {
        if (getPath().contains("?")) {
            String queryString = getPath().substring(getPathWithoutQueryString().length());

            return Arrays.stream(queryString.split("&"))
                    .map(query -> query.split("=", 2))
                    .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
        }
        return Map.of();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public Map<String, String> getBody() {
        if (body.isEmpty()) {
            return Map.of();
        }

        return Arrays.stream(body.split("&"))
                .map(query -> query.split("=", 2))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    public String build() {
        return requestLine.build() + "\r\n" + headers.build() + "\r\n\r\n" + body;
    }

    @Override
    public String toString() {
        return build();
    }
}
