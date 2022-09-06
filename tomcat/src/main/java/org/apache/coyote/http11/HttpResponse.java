package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HttpResponse {

    private static final String LINE_BREAK = "\r\n";
    private static final String EMPTY_LINE = "";
    private static final String NO_VALUE = "";
    private static final String HEADER_DELIMITER = ": ";
    private static final String HEADER_SUFFIX = " ";

    private final HttpRequest request;
    private final StatusCode statusCode;
    private final String messageBody;
    private final Map<String, String> headers = new LinkedHashMap<>();

    public HttpResponse(Builder builder) {
        this.request = builder.request;
        this.statusCode = builder.statusCode;
        this.messageBody = builder.messageBody;
        addHeaders(builder);
    }

    public String getResponse() {
        String messageHeaders = addHeaders(makeStatueLine());

        return String.join(LINE_BREAK,
            messageHeaders,
            EMPTY_LINE,
            messageBody);
    }

    private void addHeaders(Builder builder) {
        addContentType();
        addContentLength();
        addHeader("Location", builder.location);
        addHeader("Set-Cookie", builder.cookie);
    }

    public void addContentType() {
        if (request.containsHeader("Accept") && request.getHeaderValue("Accept").contains("text/css")) {
            this.headers.put("Content-Type", "text/css;");
            return;
        }
        this.headers.put("Content-Type", "text/html;charset=utf-8");
    }

    private void addContentLength() {
        this.headers.put("Content-Length", String.valueOf(messageBody.getBytes().length));
    }

    private void addHeader(String headerName, String value) {
        if (!value.isEmpty()) {
            headers.put(headerName, value);
        }
    }

    private String addHeaders(String message) {
        final StringJoiner lineJoiner = new StringJoiner(LINE_BREAK);
        lineJoiner.add(message);
        for (String headerName : headers.keySet()) {
            final StringJoiner joiner = new StringJoiner(HEADER_DELIMITER, EMPTY_LINE, HEADER_SUFFIX);
            final String headerLine = joiner.add(headerName).add(headers.get(headerName)).toString();
            lineJoiner.add(headerLine);
        }
        return lineJoiner.toString();
    }

    private String makeStatueLine() {
        StringJoiner joiner = new StringJoiner(HEADER_SUFFIX, "", HEADER_SUFFIX);
        return joiner.add(request.getHttpVersion())
            .add(String.valueOf(statusCode.getCode()))
            .add(statusCode.name())
            .toString();
    }

    public byte[] getBytes() {
        return getResponse().getBytes();
    }

    public static class Builder {

        private final HttpRequest request;

        private StatusCode statusCode = StatusCode.OK;
        private String messageBody = NO_VALUE;
        private String location = NO_VALUE;
        private String cookie = NO_VALUE;

        public Builder(HttpRequest request) {
            this.request = request;
        }

        public Builder ok() {
            this.statusCode = StatusCode.OK;
            return this;
        }

        public Builder redirect() {
            this.statusCode = StatusCode.FOUND;
            return this;
        }

        public Builder messageBody(String messageBody) {
            this.messageBody = messageBody;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder cookie(HttpCookie cookie) {
            this.cookie = cookie.getCookieHeader();
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}


