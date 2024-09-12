package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public class Request {

    private static final int CONTENT_LENGTH_WHEN_EMPTY_BODY = 0;

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestCookies cookies;
    private final RequestBody body;

    public Request(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        this.requestLine = new RequestLine(requestLine);
        this.headers = new RequestHeaders(reader);
        this.cookies = new RequestCookies(headers);
        this.body = new RequestBody(reader, getContentLength());
    }

    private int getContentLength() {
        return headers.getContentLength()
                .map(header -> Integer.parseInt(header.getValue()))
                .orElse(CONTENT_LENGTH_WHEN_EMPTY_BODY);
    }

    public Optional<RequestCookie> getLoginCookie() {
        return cookies.getLoginCookie();
    }

    public boolean hasPath(String path) {
        return requestLine.hasPath(path);
    }

    public boolean hasGetMethod() {
        return requestLine.hasGetMethod();
    }

    public boolean hasPostMethod() {
        return requestLine.hasPostMethod();
    }

    public RequestBody getBody() {
        return body;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                ", headers=" + headers +
                ", cookies=" + cookies +
                ", body=" + body +
                '}';
    }
}
