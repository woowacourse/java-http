package org.apache.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.http.info.ContentType;
import org.apache.http.info.HttpHeaderName;
import org.apache.http.info.HttpVersion;
import org.apache.http.info.StatusCode;

public class BasicHttpResponse implements HttpResponse {

    private static final String CRLF = "\r\n";

    private final HttpVersion httpVersion;
    private final StatusCode statusCode;
    private final Map<String, String> headers;
    private final String body;

    public BasicHttpResponse(final HttpVersion httpVersion, final StatusCode statusCode,
                             final Map<String, String> headers, final String body) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public static BasicHttpResponse of(final HttpVersion httpVersion, final StatusCode statusCode,
                                       final Map<String, String> headers, final String body) {
        return new BasicHttpResponse(httpVersion, statusCode, headers, body);
    }

    @Override
    public String getResponseHttpMessage() {
        return String.join(
                CRLF,
                createStartLine(),
                createHeaders(),
                createContentLength(),
                "",
                this.body);
    }

    private String createStartLine() {
        return String.format("%s %s ", this.httpVersion.getName(), this.statusCode.getName());
    }

    private String createHeaders() {
        return headers.entrySet()
                .stream()
                .map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(CRLF));
    }

    private String createContentLength() {
        return String.format("%s: %s ", HttpHeaderName.CONTENT_LENGTH.getName(), this.body.getBytes().length);
    }

    public static BasicHttpResponseBuilder builder() {
        return new BasicHttpResponseBuilder();
    }

    public static class BasicHttpResponseBuilder {

        private HttpVersion httpVersion;
        private StatusCode statusCode;
        private Map<String, String> headers;
        private String body;

        public BasicHttpResponseBuilder() {
            headers = new HashMap<>();
        }

        public BasicHttpResponse build() {
            checkEssentialValues();

            return BasicHttpResponse.of(this.httpVersion, this.statusCode, this.headers, this.body);
        }

        private void checkEssentialValues() {
            if (Objects.isNull(this.httpVersion)) {
                this.httpVersion = HttpVersion.HTTP_1_1;
            }

            if (Objects.isNull(this.statusCode)) {
                this.statusCode = StatusCode.OK_200;
            }

            if (!headers.containsKey(HttpHeaderName.CONTENT_TYPE.getName())) {
                headers.put(HttpHeaderName.CONTENT_LENGTH.getName(), ContentType.TEXT_HTML.getName());
            }
        }

        public BasicHttpResponseBuilder httpVersion(final HttpVersion httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public BasicHttpResponseBuilder statusCode(final StatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public BasicHttpResponseBuilder setCookie(final String cookie) {
            headers.put(HttpHeaderName.SET_COOKIE.getName(), cookie);
            return this;
        }

        public BasicHttpResponseBuilder cacheControl(final String cacheControl) {
            headers.put(HttpHeaderName.CACHE_CONTROL.getName(), cacheControl);
            return this;
        }

        public BasicHttpResponseBuilder contentType(final String contentType) {
            headers.put(HttpHeaderName.CONTENT_TYPE.getName(), contentType);
            return this;
        }

        public BasicHttpResponseBuilder addHeader(final String headerName, final String headerValue) {
            headers.put(headerName, headerValue);
            return this;
        }

        public BasicHttpResponseBuilder body(final String body) {
            this.body = body;
            return this;
        }
    }
}
