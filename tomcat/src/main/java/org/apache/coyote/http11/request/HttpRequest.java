package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.session.Cookies;

public class HttpRequest {

    private static final String COOKIE_HEADER_KEY = "Cookie";
    private static final String COOKIE_PARAMETER_DELIMITER = "=";
    private static final String COOKIE_CONNECTOR = "; ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

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

    public boolean hasRequestBody() {
        return !requestBody.isEmpty();
    }

    public Optional<String> getSession() {
        Optional<String> cookieValue = requestHeaders.getHeaderValue(COOKIE_HEADER_KEY);
        if (cookieValue.isEmpty()) {
            return Optional.empty();
        }
        return extractSessionValue(cookieValue.get());
    }

    private Optional<String> extractSessionValue(String cookieValue) {
        Map<String, String> cookies = Arrays.stream(cookieValue.split(COOKIE_CONNECTOR))
                .collect(Collectors.toMap(cookie -> cookie.split(COOKIE_PARAMETER_DELIMITER)[KEY_INDEX],
                        cookie -> cookie.split(COOKIE_PARAMETER_DELIMITER)[VALUE_INDEX]));
        if (cookies.containsKey(Cookies.JSESSIONID)) {
            return Optional.of(cookies.get(Cookies.JSESSIONID));
        }
        return Optional.empty();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
