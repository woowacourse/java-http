package org.apache.coyote.response;

import org.apache.coyote.common.Headers;
import org.apache.coyote.session.Cookies;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.coyote.common.CharacterSet.UTF_8;
import static org.apache.coyote.common.HeaderType.CONTENT_LENGTH;
import static org.apache.coyote.common.HeaderType.CONTENT_TYPE;
import static org.apache.coyote.common.HeaderType.LOCATION;
import static org.apache.coyote.common.HeaderType.SET_COOKIE;

public class ResponseHeaders {

    private final Headers headers;

    private ResponseHeaders(final Headers headers) {
        this.headers = headers;
    }

    public static ResponseHeaders empty() {
        return new ResponseHeaders(Headers.empty());
    }

    public void addHeader(final String headerName, final String headerValue) {
        headers.addHeader(headerName, headerValue);
    }

    public String getHeaderValue(final String headerName) {
        return headers.getHeaderValue(headerName);
    }

    public ResponseHeaders setContentType(final String contentType) {
        this.headers.addHeader(CONTENT_TYPE.value(), contentType + ";" + UTF_8.value());
        return this;
    }

    public ResponseHeaders setContentLength(final int contentLength) {
        this.headers.addHeader(CONTENT_LENGTH.value(), String.valueOf(contentLength));
        return this;
    }

    public ResponseHeaders setLocation(final String uri) {
        this.headers.addHeader(LOCATION.value(), uri);
        return this;
    }

    public ResponseHeaders setCookies(final Cookies cookies) {
        final String cookieValues = cookies.cookieNames()
                .stream()
                .map(cookieName -> cookieName + "=" + cookies.getCookieValue(cookieName))
                .collect(Collectors.joining(";"));

        headers.addHeader(SET_COOKIE.value(), cookieValues);
        return this;
    }

    public List<String> headerNames() {
        return headers.headerNames();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ResponseHeaders that = (ResponseHeaders) o;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    @Override
    public String toString() {
        return "ResponseHeaders{" +
               "headers=" + headers +
               '}';
    }
}
