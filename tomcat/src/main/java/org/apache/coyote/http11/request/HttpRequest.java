package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final HttpMethod method;
    private final String uri;
    private final RequestHeader header;
    private RequestBody body;

    private HttpRequest(HttpMethod method, String uri, RequestHeader header, RequestBody body) {
        this.method = method;
        this.uri = uri;
        this.header = header;
        this.body = body;
    }

    private HttpRequest(HttpMethod method, String uri, RequestHeader header) {
        this(method, uri, header, null);
    }

    public static HttpRequest of(final String requestLine, final BufferedReader reader) throws IOException {
        final var parts = requestLine.split(" ");
        final var method = HttpMethod.valueOf(parts[0]);
        final var uri = parts[1];
        final var header = RequestParser.getRequestHeader(reader);
        if (method.isPost()) {
            final var body = RequestParser.getRequestBody(reader, header.getContentLength());
            return new HttpRequest(method, uri, header, body);
        }
        return new HttpRequest(method, uri, header);
    }

    public boolean isGet() {
        return method.isGet();
    }

    public boolean isPost() {
        return method.isPost();
    }

    public boolean isSameUri(final String uri) {
        return this.uri.equals(uri);
    }

    public String findBodyValueByKey(final String key) {
        return body.findByKey(key);
    }

    public String getCookie() {
        return getHeader("Cookie");
    }

    public String getHeader(final String name) {
        return header.findHeader(name);
    }

    public String getUri() {
        return uri;
    }
}
