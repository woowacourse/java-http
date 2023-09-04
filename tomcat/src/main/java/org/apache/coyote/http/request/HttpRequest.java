package org.apache.coyote.http.request;

import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http.HttpHeader;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http.cookie.Cookie;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpHeader header;
    private final Map<String, String> parameters;

    public HttpRequest(HttpRequestLine requestLine, HttpHeader header,
        Map<String, String> parameters) {
        this.requestLine = requestLine;
        this.header = header;
        parameters = new HashMap<>(parameters);
        parameters.putAll(requestLine.getParameters());
        this.parameters = parameters;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public Map<String, String> getParameters() {
        return new HashMap<>(parameters);
    }

    public Optional<String> getSessionId() {
        return header.findSessionIdCookie()
                     .map(Cookie::getValue);
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
        return Objects.equals(requestLine, that.requestLine) && Objects.equals(
            header, that.header) && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, header, parameters);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
            "requestLine=" + requestLine +
            ", header=" + header +
            ", parameters=" + parameters +
            '}';
    }
}
