package org.apache.coyote.http11;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.Cookie;
import org.apache.coyote.http11.header.HeaderType;
import org.apache.coyote.http11.header.HttpStatus;
import org.apache.coyote.http11.header.HttpVersion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private static final String HEADER_SEPARATOR = ": ";
    private final HttpResponseBuilder builder = new HttpResponseBuilder();
    private HttpStatus status;

    public HttpResponseBuilder status(final HttpStatus status) {
        this.status = status;
        builder.status(status);
        return builder;
    }

    public String build() {
        return builder.build();
    }

    public int getStatus() {
        return status.getCode();
    }

    public static class HttpResponseBuilder {
        private final Map<HeaderType, String> headers = new LinkedHashMap<>();
        private HttpStatus status;
        private String body;

        public void status(final HttpStatus status) {
            this.status = status;
        }

        public HttpResponseBuilder contentType(final ContentType contentType) {
            headers.put(HeaderType.CONTENT_TYPE, contentType.getType());
            return this;
        }

        public HttpResponseBuilder location(final String location) {
            headers.put(HeaderType.LOCATION, location);
            return this;
        }

        public HttpResponseBuilder body(final String responseBody) {
            contentLength(responseBody.getBytes().length);
            this.body = responseBody;
            return this;
        }

        public HttpResponseBuilder contentLength(final int length) {
            headers.put(HeaderType.CONTENT_LENGTH, String.valueOf(length));
            return this;
        }

        public String build() {
            List<String> response = new ArrayList<>();
            response.add(HttpVersion.HTTP_1_1.getVersion() + " " + status.getStatusResponse() + " ");
            headers.forEach((key, value) -> response.add(key.getHeader() + HEADER_SEPARATOR + value + " "));
            response.add("");
            response.add(body);
            return String.join("\r\n", response);
        }

        public HttpResponseBuilder setCookie(final Cookie cookie) {
            headers.put(HeaderType.SET_COOKIE, cookie.getName() + "=" + cookie.getValue());
            return this;
        }
    }
}
