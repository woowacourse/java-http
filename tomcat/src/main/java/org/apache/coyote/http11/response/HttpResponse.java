package org.apache.coyote.http11.response;

import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpHeaders;

public class HttpResponse {

    private static final String EMPTY_LINE = "";
    private static final String NEW_LINE = "\r\n";
    private static final String COOKIES_DELIMITER = "; ";
    private final HttpResponseCookies cookies;
    private final HttpResponseHeaders headers;
    private HttpResponseStartLine httpResponseStartLine;
    private String responseBody;

    public HttpResponse() {
        this.cookies = HttpResponseCookies.empty();
        this.headers = HttpResponseHeaders.empty();
    }

    public void sendRedirect(final String location) {
        httpResponseStartLine = HttpResponseStartLine.of(StatusCode.FOUND);
        headers.add(HttpHeaders.LOCATION, location);
    }

    public void addCookie(final String key, final String value) {
        cookies.add(key, value);
    }

    public void addHeader(final String name, final String value) {
        headers.add(name, value);
    }

    public void setHttpResponseStartLine(final StatusCode statusCode) {
        httpResponseStartLine = HttpResponseStartLine.of(statusCode);
    }

    public void setResponseBody(final byte[] responseBody) {
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(responseBody.length));
        this.responseBody = new String(responseBody);
    }

    public byte[] generateResponse() {
        return String.join(NEW_LINE,
                generateStartLine(),
                generateHeaders(),
                EMPTY_LINE,
                responseBody).getBytes();
    }

    private String generateStartLine() {
        return String.format("%s %s %s",
                httpResponseStartLine.getHttpVersion(),
                httpResponseStartLine.getStatusCode().getCode(),
                httpResponseStartLine.getStatusCode().getText()
        );
    }

    private String generateHeaders() {
        String cookiesValue = generateCookies();
        if (!cookiesValue.isBlank()) {
            headers.add(HttpHeaders.SET_COOKIE, cookiesValue);
        }
        return headers.getEntrySet()
                .stream()
                .map(header -> String.format("%s: %s", header.getKey(), header.getValue()))
                .collect(Collectors.joining(NEW_LINE));
    }

    private String generateCookies() {
        return cookies.getEntrySet()
                .stream()
                .map(header -> String.format("%s=%s", header.getKey(), header.getValue()))
                .collect(Collectors.joining(COOKIES_DELIMITER));
    }
}
