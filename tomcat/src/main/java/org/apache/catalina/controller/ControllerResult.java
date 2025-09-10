package org.apache.catalina.controller;

import static org.apache.coyote.http11.HttpConstants.EMPTY;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.dto.response.Status;

public class ControllerResult {

    private final Status status;
    private final Map<String, String> headers;
    private final String body;
    private final boolean requireSession;

    private ControllerResult(final Builder builder) {
        this.status = builder.status;
        this.headers = builder.headers;
        this.body = builder.body;
        this.requireSession = builder.requireSession;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ControllerResult of(final Status status) {
        return ControllerResult.builder()
                .status(status)
                .body(status.line())
                .build();
    }

    public Status status() {
        return status;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public String body() {
        return body;
    }

    public boolean requireSession() {
        return requireSession;
    }

    public static class Builder {

        private final Map<String, String> headers = new LinkedHashMap<>();
        private Status status;
        private String body = EMPTY;
        private boolean requireSession = false;

        public Builder status(final Status status) {
            this.status = status;
            return this;
        }

        public Builder header(final String name, final String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public Builder requireSession(final boolean requireSession) {
            this.requireSession = requireSession;
            return this;
        }

        public ControllerResult build() {
            return new ControllerResult(this);
        }
    }
}
