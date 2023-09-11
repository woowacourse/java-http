package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseHeader {

    public static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
    public static final String TEXT_CSS_CHARSET_UTF_8 = "text/css;charset=utf-8";
    private final Map<String, String> headers;

    public HttpResponseHeader(final Builder httpResponseHeaderBuilder) {
        this.headers = httpResponseHeaderBuilder.headers;
    }

    @Override
    public String toString() {
        List<String> header = headers.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> entry.getKey() + ": " + entry.getValue()
                ).collect(Collectors.toList());
        return String.join(" \r\n", header);
    }

    public static class Builder {
        private Map<String, String> headers;

        public Builder(final String contentType, final String contentLength) {
            this.headers = new HashMap<>();
            this.headers.put("Content-Type", contentType);
            this.headers.put("Content-Length", contentLength);
        }

        public Builder addLocation(final String location) {
            this.headers.put("Location", location);
            return this;
        }

        public Builder addSetCookie(final String setCookie) {
            this.headers.put("Set-Cookie", setCookie);
            return this;
        }

        public HttpResponseHeader build() {
            return new HttpResponseHeader(this);
        }
    }
}
