package org.apache.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public final class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";
    private static final String HEADER_DELIMITER = ": ";
    private static final String SPACE = " ";
    private static final String CRLF = "\r\n";

    private final OutputStream outputStream;
    private HttpStatus httpStatus;
    private final HttpHeaders responseHeaders;
    private final Cookie cookie;

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
        this.responseHeaders = new HttpHeaders(new HashMap<>());
        this.cookie = Cookie.empty();
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void addHeader(final String headerName, final String headerValue) {
        this.responseHeaders.add(headerName, headerValue);
    }

    public void addCookie(final String key, final String value) {
        cookie.addCookie(key, value);
    }

    public String toString() {
        final StringBuilder responseBuilder = new StringBuilder(PROTOCOL_VERSION + SPACE + httpStatus.getStatusCode() + SPACE + httpStatus.getStatusString() + SPACE);
        final List<String> headers = responseHeaders.getAllHeaders()
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + HEADER_DELIMITER + entry.getValue() + SPACE)
                .collect(Collectors.toList());
        if (!headers.isEmpty()) {
            final String joinedAllHeaders = String.join(CRLF, headers);
            responseBuilder.append(CRLF).append(joinedAllHeaders);
        }
        if (!cookie.isEmpty()) {
            responseBuilder.append(CRLF).append(HttpHeaders.SET_COOKIE + HEADER_DELIMITER + cookie.generateCookieHeaderValue() + SPACE);
        }

        return responseBuilder.toString();
    }

    public void sendRedirect(final String location) throws IOException {
        setHttpStatus(HttpStatus.FOUND);
        responseHeaders.add(HttpHeaders.LOCATION, location);
        outputStream.write(toString().getBytes(StandardCharsets.UTF_8));
    }

    public void write(final String responseBody) throws IOException {
        outputStream.write(
                String.join(CRLF,
                        toString(),
                        HttpHeaders.CONTENT_LENGTH + HEADER_DELIMITER + responseBody.getBytes(StandardCharsets.UTF_8).length + SPACE,
                        "",
                        responseBody
                ).getBytes(StandardCharsets.UTF_8)
        );
    }
}
