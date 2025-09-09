package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private final int statusCode;
    private final String statusMessage;
    private final String contentType;
    private final byte[] body;
    private final Map<String, List<String>> headers;

    private HttpResponse(
            final int statusCode,
            final String statusMessage,
            final String contentType,
            final byte[] body,
            final Map<String, List<String>> headers
    ) {
        Objects.requireNonNull(statusMessage, "statusMessage must not be null");
        Objects.requireNonNull(contentType, "contentType must not be null");
        Objects.requireNonNull(headers, "headers must not be null");
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.contentType = contentType;
        this.body = body;
        this.headers = headers;
    }

    public void writeTo(final OutputStream outputStream) throws IOException {
        final var response = new StringBuilder();

        response.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage)
                .append(CRLF);

        appendLine(response, "Content-Type", contentType);

        final int length = body == null ? 0 : body.length;
        appendLine(response, "Content-Length", String.valueOf(length));

        for (var entry : headers.entrySet()) {
            var name = entry.getKey();
            var values = entry.getValue();
            for (var value : values) {
                appendLine(response, name, value);
            }
        }

        response.append(CRLF);
        outputStream.write(response.toString()
                .getBytes(StandardCharsets.UTF_8));

        if (body != null) {
            outputStream.write(body);
        }

        outputStream.flush();
    }

    private void appendLine(final StringBuilder builder, final String name, final String value) {
        builder.append(name)
                .append(": ")
                .append(value)
                .append(CRLF);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private int statusCode;
        private String statusMessage;
        private String contentType = "text/plain; charset=utf-8";
        private byte[] body;
        private final Map<String, List<String>> headers = new LinkedHashMap<>();

        public Builder status(final int statusCode, final String statusMessage) {
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder contentType(final String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder body(final byte[] body) {
            this.body = body;
            return this;
        }

        public Builder headers(final Map<String, List<String>> headers) {
            headers.forEach((key, values) -> values.forEach(value -> header(key, value)));
            return this;
        }

        public Builder header(final String name, final String value) {
            headers.computeIfAbsent(name, k -> new ArrayList<>())
                    .add(value);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusCode, statusMessage, contentType, body, headers);
        }
    }
}
