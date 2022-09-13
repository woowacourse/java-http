package org.apache.coyote.http11.request;

import java.util.Optional;
import org.apache.coyote.http11.session.Cookie;

public class HttpRequest {

    private static final String COOKIE_HEADER_KEY = "Cookie";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public boolean isStaticFileRequest() {
        return requestLine.isStaticFileRequest();
    }

    public Optional<String> getExtension() {
        return requestLine.getExtension();
    }

    public Optional<String> getSession() {
        Optional<String> cookieValue = requestHeaders.getHeaderValue(COOKIE_HEADER_KEY);
        if (cookieValue.isEmpty()) {
            return Optional.empty();
        }
        return extractSessionValue(cookieValue.get());
    }

    private Optional<String> extractSessionValue(String cookieValue) {
        Cookie cookie = Cookie.of(cookieValue);
        return cookie.getJSessionValue();
    }

    public String getPath() {
        URI uri = requestLine.getUri();
        return uri.getPath();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
