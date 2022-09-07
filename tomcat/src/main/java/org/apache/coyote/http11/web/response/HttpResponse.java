package org.apache.coyote.http11.web.response;

import static org.apache.coyote.http11.support.HttpHeader.*;
import static org.apache.coyote.http11.support.HttpMime.TEXT_HTML;

import org.apache.coyote.http11.file.ResourceLoader;
import org.apache.coyote.http11.support.HttpCookie;
import org.apache.coyote.http11.support.HttpHeader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStatus;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class HttpResponse {

    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";
    private static final String EMPTY_BODY = "";

    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;
    private final String responseBody;

    public HttpResponse(final HttpStatus httpStatus,
                        final HttpHeaders httpHeaders,
                        final String responseBody) {
        initializeHeaders(httpHeaders, responseBody);
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse sendRedirectWithCookie(final String uri, final HttpCookie httpCookie) {
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(LOCATION, uri);
        httpHeaders.put(SET_COOKIE, httpCookie.format());

        return new HttpResponse(HttpStatus.FOUND, httpHeaders, EMPTY_BODY);
    }

    public static HttpResponse sendRedirect(final String uri) {
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(LOCATION, uri);

        return new HttpResponse(HttpStatus.FOUND, httpHeaders, EMPTY_BODY);
    }

    public static HttpResponse sendError(final HttpStatus httpStatus) throws IOException {
        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        final String statusCode = httpStatus.getStatusCode();

        return new HttpResponse(httpStatus, httpHeaders, ResourceLoader.getContent(statusCode + ".html"));
    }

    public String format() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%s %s %s \r\n",
                        HTTP_VERSION_1_1, httpStatus.getStatusCode(), httpStatus.name()))
                .append(formatHeader())
                .append("\r\n")
                .append(responseBody);

        return stringBuilder.toString();
    }

    private void initializeHeaders(final HttpHeaders httpHeaders, final String responseBody) {
        if (!httpHeaders.existsContentType()) {
            httpHeaders.put(CONTENT_TYPE, TEXT_HTML.getValue());
        }
        httpHeaders.put(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
    }

    private StringBuilder formatHeader() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Map<HttpHeader, String> headers = httpHeaders.getValues();
        headers.forEach((key, value) -> stringBuilder.append(String.format("%s: %s \r\n", key.getValue(), value)));

        return stringBuilder;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof HttpResponse)) return false;
        final HttpResponse that = (HttpResponse) o;
        return httpStatus == that.httpStatus && Objects.equals(httpHeaders, that.httpHeaders) && Objects.equals(responseBody, that.responseBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpStatus, httpHeaders, responseBody);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "httpStatus=" + httpStatus +
                ", httpHeaders=" + httpHeaders +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}
