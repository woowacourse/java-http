package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String LOCATION = "Location";
    private static final String EMPTY = "";
    private static final String BLANK = " ";
    private static final String COLON = ": ";
    private static final String EQUAL = "=";
    private static final String SEPARATOR = "\r\n";

    private final byte[] bytes;

    private HttpResponse(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Builder status(HttpStatus httpStatus) {
        return new Builder(httpStatus);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public static class Builder {

        private final HttpStatus httpStatus;
        private final HttpCookie httpCookie;
        private ContentType contentType;
        private String redirectUri;
        private String body;

        private Builder(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            httpCookie = HttpCookie.empty();
        }

        public Builder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder cookie(String name, String value) {
            httpCookie.addCookie(name, value);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            Map<String, String> headers = new LinkedHashMap<>();
            if (contentType != null) {
                headers.put(CONTENT_TYPE, contentType.value());
            }
            if (redirectUri != null) {
                headers.put(LOCATION, redirectUri);
            }
            if (body != null) {
                headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
            }
            return new HttpResponse(getBytes(headers));
        }

        private byte[] getBytes(Map<String, String> headers) {
            List<String> response = new ArrayList<>();
            response.add(HTTP_VERSION + BLANK + httpStatus.statusCode() + BLANK + httpStatus + BLANK);
            response.add(stringify(headers));
            httpCookie.getCookie(JSESSIONID)
                    .ifPresent(id -> response.add(SET_COOKIE + COLON + JSESSIONID + EQUAL + id + BLANK));
            response.add(EMPTY);
            response.add(body);
            return String.join(SEPARATOR, response).getBytes();
        }

        private String stringify(Map<String, String> headers) {
            List<String> formattedHeaders = headers.keySet().stream()
                    .map(key -> key + COLON + headers.get(key) + BLANK)
                    .collect(Collectors.toList());
            return String.join(SEPARATOR, formattedHeaders);
        }
    }
}
