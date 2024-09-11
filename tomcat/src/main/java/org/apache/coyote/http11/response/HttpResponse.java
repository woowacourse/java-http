package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HttpResponse {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String STATUS_LINE_FORMAT = "%s %d %s ";
    private static final String HEADER_FORMAT = "%s: %s ";
    private static final String REDIRECTION_HEADER = "Location";
    private static final String COOKIE_HEADER = "Set-Cookie";
    private static final HttpStatus DEFAULT_REDIRECT_STATUS = HttpStatus.FOUND;

    private final Protocol protocol = Protocol.HTTP11;
    private HttpStatus httpStatus;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this(null, new HashMap<>(), "");
    }

    public HttpResponse(HttpStatus httpStatus) {
        this(httpStatus, new HashMap<>(), "");
    }

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers) {
        this(httpStatus, headers, "");
    }

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers, String body) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse createRedirectResponse(HttpStatus httpStatus, String location) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", location);
        return new HttpResponse(
                httpStatus,
                headers
        );
    }

    public static HttpResponse createTextResponse(HttpStatus httpStatus, String responseBody) {
        Map<String, String> headers = new HashMap<>();
        int contentLength = responseBody.getBytes().length;
        headers.put("Content-Type", "text/plain;charset=utf-8 ");
        headers.put("Content-Length", String.valueOf(contentLength));

        return new HttpResponse(
                httpStatus,
                headers,
                responseBody
        );
    }

    public void redirectTo(String location) {
        redirectTo(location, DEFAULT_REDIRECT_STATUS);
    }

    public void redirectTo(String location, HttpStatus status) {
        addRedirectHeader(location);
        setHttpStatus(status);
    }

    private void addRedirectHeader(String location) {
        addHeader(REDIRECTION_HEADER, location);
    }

    public void addCookie(ResponseCookie cookie) {
        addHeader(COOKIE_HEADER, cookie.toResponse());
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String toResponse() {
        String statusLine = createStatusLineResponse();
        String headers = createHeadersResponse();

        return String.join(
                LINE_SEPARATOR,
                statusLine,
                headers,
                "",
                body
        );
    }

    private String createStatusLineResponse() {
        return String.format(STATUS_LINE_FORMAT,
                protocol.getName(),
                httpStatus.getCode(),
                httpStatus.getReasonPhrase()
        );
    }

    private String createHeadersResponse() {
        List<String> formattedHeaders = new ArrayList<>();
        for (String headerKey : headers.keySet()) {
            String headerValue = headers.get(headerKey);
            String formattedHeader = String.format(HEADER_FORMAT, headerKey, headerValue);
            formattedHeaders.add(formattedHeader);
        }
        return String.join(LINE_SEPARATOR, formattedHeaders);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void addFile(ResponseFile responseFile) {
        headers.put("Content-Type", responseFile.getContentType());
        headers.put("Content-Length", String.valueOf(responseFile.getContentLength()));
        body = responseFile.getContent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpResponse response = (HttpResponse) o;
        return protocol == response.protocol && httpStatus == response.httpStatus && Objects.equals(headers,
                response.headers) && Objects.equals(body, response.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, httpStatus, headers, body);
    }
}
