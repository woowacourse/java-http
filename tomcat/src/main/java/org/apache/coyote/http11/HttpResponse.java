package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpStatus;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class HttpResponse {

    private HttpStatus status;
    private final Map<String, String> headers;
    private byte[] body;

    public HttpResponse() {
        this.status = HttpStatus.OK;
        this.headers = new LinkedHashMap<>();
        this.body = new byte[0];
    }

    public HttpStatus status() {
        return status;
    }

    public Map<String, String> headers() {
        return Collections.unmodifiableMap(headers);
    }

    public byte[] body() {
        return body.clone();
    }

    public void setStatus(final HttpStatus status) {
        this.status = Objects.requireNonNull(
                status,
                "status는 null일 수 없습니다."
        );
    }

    public void addHeader(
            final String name,
            final String value
    ) {
        headers.put(
                Objects.requireNonNull(name, "header name은 null일 수 없습니다."),
                Objects.requireNonNull(value, "header value는 null일 수 없습니다.")
        );
    }

    public void setBody(final byte[] body) {
        this.body = Objects.requireNonNull(
                body,
                "body는 null일 수 없습니다."
        ).clone();
    }
}