package org.apache.coyote.http11.httpmessage.request;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Pattern;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;

    private HttpRequest(RequestLine requestLine, Headers headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public static HttpRequest of(String request) {
        LinkedList<String> lines = getLines(request);

        RequestLine requestLine = RequestLine.of(lines.remove());
        Headers headers = Headers.of(lines);

        return new HttpRequest(requestLine, headers);
    }

    private static LinkedList<String> getLines(String request) {
        return new LinkedList<>(Arrays.asList(request.split("\r\n")));
    }

    public boolean matchUri(Pattern uriPattern) {
        return requestLine.matchUri(uriPattern);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Object getParameter(String key) {
        return requestLine.getParameter(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpRequest that = (HttpRequest) o;
        return Objects.equals(requestLine, that.requestLine) && Objects.equals(headers,
                that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, headers);
    }
}
