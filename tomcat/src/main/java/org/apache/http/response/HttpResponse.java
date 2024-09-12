package org.apache.http.response;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.http.HttpCookie;
import org.apache.http.HttpVersion;
import org.apache.http.header.HttpHeader;
import org.apache.http.header.HttpHeaderName;
import org.apache.http.header.HttpHeaders;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private final StartLine startLine;
    private final HttpHeaders headers;
    private final String responseBody;

    public HttpResponse(StartLine startLine, HttpHeaders headers, String responseBody) {
        this.startLine = startLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse from(String startLine, HttpHeader[] headers, String responseBody) {
        return new HttpResponse(StartLine.from(startLine), new HttpHeaders(headers), responseBody);
    }

    public static HttpResponse.Builder builder() {
        return new Builder();
    }

    public HttpVersion getVersion() {
        return startLine.getVersion();
    }

    public HttpStatus getStatus() {
        return startLine.getHttpStatus();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public HttpCookie getCookie() {
        return headers.getCookie();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpResponse that = (HttpResponse) o;
        return Objects.equals(startLine, that.startLine) && Objects.equals(headers, that.headers)
                && Objects.equals(responseBody, that.responseBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startLine, headers, responseBody);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(startLine.toString());
        stringBuilder.append(headers.toString()).append(CRLF);

        if (responseBody == null) {
            return stringBuilder.toString();
        }

        stringBuilder.append(responseBody);
        return stringBuilder.toString();
    }

    public static class Builder {
        private HttpStatus status = HttpStatus.OK;
        private HttpVersion version = HttpVersion.HTTP_1_1;
        private Set<HttpHeader> headers = new HashSet<>();
        private String body = "";

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder version(HttpVersion version) {
            this.version = version;
            return this;
        }

        public Builder addHeader(HttpHeaderName name, String value) {
            headers.add(new HttpHeader(name, value));
            return this;
        }

        public Builder addLocation(String value) {
            headers.add(new HttpHeader(HttpHeaderName.LOCATION, value));
            return this;
        }

        public Builder addCookie(String cookieKey, String cookieValue) {
            Optional<HttpHeader> cookieHeader = findCookieHeader();

            if (cookieHeader.isPresent()) {
                addToExistsCookie(cookieHeader.get(), cookieKey, cookieValue);
                return this;
            }

            String cookieHeaderValue = new HttpCookie(cookieKey, cookieValue).toString();
            headers.add(new HttpHeader(HttpHeaderName.SET_COOKIE, cookieHeaderValue));
            return this;
        }

        private Optional<HttpHeader> findCookieHeader() {
            return headers.stream()
                    .filter(header -> header.getKey() == HttpHeaderName.SET_COOKIE)
                    .findFirst();
        }

        private void addToExistsCookie(HttpHeader cookieHeader, String cookieKey, String cookieValue) {
            headers.remove(cookieHeader);

            HttpCookie httpCookie = HttpCookie.from(cookieHeader.getValue());
            httpCookie.add(cookieKey, cookieValue);
            headers.add(new HttpHeader(HttpHeaderName.SET_COOKIE, httpCookie.toString()));
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            StartLine startLine = new StartLine(version, status);
            return new HttpResponse(startLine, new HttpHeaders(headers.toArray(HttpHeader[]::new)), body);
        }

        public HttpResponse okBuild() {
            status = HttpStatus.OK;
            return this.build();
        }

        public HttpResponse foundBuild() {
            status = HttpStatus.FOUND;
            return this.build();
        }

        public HttpResponse internalServerErrorBuild() {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            return this.build();
        }

        public HttpResponse notFoundBuild() {
            status = HttpStatus.NOT_FOUND;
            return this.build();
        }

        public HttpResponse unauthorizedBuild() {
            status = HttpStatus.UNAUTHORIZED;
            return this.build();
        }
    }
}
