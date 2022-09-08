package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;

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

    public HttpResponse(final Builder builder) {
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

    private void addHeaders(final Builder builder) {
        addHeader("Content-Type", ContentType.from(request));
        addHeader("Content-Length", String.valueOf(messageBody.getBytes().length));
        addHeader("Location", builder.location);
        addHeader("Set-Cookie", builder.cookie);
    }

    private void addHeader(final String headerName, final String value) {
        if (!value.isEmpty()) {
            headers.put(headerName, value);
        }
    }

    private String addHeaders(final String message) {
        final StringJoiner lineJoiner = new StringJoiner(LINE_BREAK);
        lineJoiner.add(message);
        for (String headerName : headers.keySet()) {
            final StringJoiner joiner = new StringJoiner(HEADER_DELIMITER, "", HEADER_SUFFIX);
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

        public Builder(final HttpRequest request) {
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

        public Builder messageBody(final String messageBody) {
            this.messageBody = messageBody;
            return this;
        }

        public Builder location(final String location) {
            this.location = location;
            return this;
        }

        public Builder cookie(final HttpCookie cookie) {
            this.cookie = cookie.getJSessionCookieHeader();
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}


