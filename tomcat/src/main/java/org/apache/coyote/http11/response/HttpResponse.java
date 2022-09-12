package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.Resource;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

    private static final String LINE_BREAK = "\r\n";
    private static final String EMPTY_LINE = "";
    private static final String NO_VALUE = "";
    private static final String HEADER_DELIMITER = ": ";
    private static final String HEADER_SUFFIX = " ";

    private final HttpRequest request;
    private final StatusLine statusLine;
    private final String messageBody;
    private final Map<String, String> headers = new LinkedHashMap<>();

    public HttpResponse(final Builder builder) {
        this.request = builder.request;
        this.statusLine = StatusLine.of(request, builder.statusCode);
        this.messageBody = builder.messageBody;
        addHeaders(builder);
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

    public byte[] getBytes() {
        return makeResponse().getBytes();
    }

    public String makeResponse() {
        final String messageHead = makeMessageHead(statusLine);

        return String.join(LINE_BREAK,
            messageHead,
            EMPTY_LINE,
            messageBody);
    }

    private String makeMessageHead(final StatusLine statusLine) {
        final StringJoiner lineJoiner = new StringJoiner(LINE_BREAK);
        lineJoiner.add(statusLine.generate());
        for (String headerName : headers.keySet()) {
            final StringJoiner joiner = new StringJoiner(HEADER_DELIMITER, "", HEADER_SUFFIX);
            final String headerLine = joiner.add(headerName).add(headers.get(headerName)).toString();
            lineJoiner.add(headerLine);
        }
        return lineJoiner.toString();
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

        public Builder messageBody(final Resource resource) {
            this.messageBody = resource.getValue();
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


