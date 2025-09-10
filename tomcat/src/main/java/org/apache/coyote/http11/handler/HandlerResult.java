package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.HttpConstants.EQUAL;
import static org.apache.coyote.http11.HttpConstants.SET_COOKIE_HEADER;

import java.util.LinkedHashMap;
import java.util.Map;

public class HandlerResult {

    private final Status status;
    private final ContentType contentType;
    private final Map<String, String> headers;
    private final byte[] body;
    private final boolean requiresSession;

    private HandlerResult(final Builder builder) {
        this.status = builder.status;
        this.contentType = builder.contentType;
        this.headers = Map.copyOf(builder.headers);
        this.body = builder.body;
        this.requiresSession = builder.requiresSession;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Status status() {
        return status;
    }

    public ContentType contentType() {
        return contentType;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public byte[] body() {
        return body;
    }

    public boolean requiresSession() {
        return requiresSession;
    }

    public static class Builder {

        private Status status;
        private ContentType contentType = ContentType.TEXT;
        private final Map<String, String> headers = new LinkedHashMap<>();
        private byte[] body = new byte[0];
        private boolean requiresSession = false;

        public Builder status(final Status status) {
            this.status = status;
            return this;
        }

        public Builder contentType(final ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder header(final String name, final String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder body(final byte[] body) {
            this.body = body;
            return this;
        }

        public Builder requiresSession(final boolean requiresSession) {
            this.requiresSession = requiresSession;
            return this;
        }

        public Builder cookie(final String name, final String value) {
            this.headers.put(SET_COOKIE_HEADER, name + EQUAL + value);
            return this;
        }

        public HandlerResult build() {
            return new HandlerResult(this);
        }
    }
}
