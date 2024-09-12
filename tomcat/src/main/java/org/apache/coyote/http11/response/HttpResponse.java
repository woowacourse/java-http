package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.cookie.HttpCookie;

public class HttpResponse {

    private static final String TEXT_CONTENT_TYPE = "text/plain;charset=utf-8 ";
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String STATUS_LINE_FORMAT = "%s %d %s ";
    private static final String HEADER_FORMAT = "%s: %s ";
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

    public void redirectTo(String location) {
        redirectTo(location, DEFAULT_REDIRECT_STATUS);
    }

    public void redirectTo(String location, HttpStatus status) {
        addRedirectHeader(location);
        setHttpStatus(status);
    }

    private void addRedirectHeader(String location) {
        addHeader(HttpHeader.LOCATION, location);
    }

    public void addCookie(HttpCookie cookie) {
        addHeader(HttpHeader.SET_COOKIE, cookie.toResponse());
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setTextBody(String body) {
        setBody(TEXT_CONTENT_TYPE, body);
    }

    public void setBody(String contentType, String body) {
        this.body = body;
        int contentLength = body.getBytes().length;
        headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(contentLength));
        headers.put(HttpHeader.CONTENT_TYPE, contentType);
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

    @Override
    public String toString() {
        return "HttpResponse{" +
                "protocol=" + protocol +
                ", httpStatus=" + httpStatus +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
