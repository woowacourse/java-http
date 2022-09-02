package org.apache.coyote.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;

public abstract class Response {

    private static final String DEFAULT_VERSION = "HTTP/1.1";
    protected static final String HEADER_TEMPLATE = "%s: %s \r\n";

    private final String version;
    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;
    private List<Cookie> cookies = new ArrayList<>();

    public Response(final HttpStatus httpStatus, final HttpHeaders httpHeaders) {
        this(DEFAULT_VERSION, httpStatus, httpHeaders);
    }

    public Response(final String version, final HttpStatus httpStatus, final HttpHeaders httpHeaders) {
        this.version = version;
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
    }

    public abstract String createHttpResponse();

    public void addCookie(final Cookie cookie) {
        cookies.stream()
                .filter(element -> element.getKey().equals(cookie.getKey()))
                .findFirst()
                .ifPresent(element -> cookies.remove(element));
        cookies.add(cookie);
    }

    protected String getRequestLine() {
        return String.format("%s %d %s \r\n", version, httpStatus.getStatusCode(), httpStatus.getMessage());
    }

    protected String getCookieTemplate() {
        return cookies.stream()
                .map(cookie -> cookie.getKey() + "=" + cookie.getValue())
                .collect(Collectors.joining("; "));
    }

    public String getVersion() {
        return version;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }
}
