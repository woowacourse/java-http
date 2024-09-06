package org.apache.coyote.http11.request;

import java.util.Optional;

public class RequestBody {

    private final Optional<String> body;

    public RequestBody(Optional<String> body) {
        this.body = body;
    }

    public RequestBody(String body) {
        this.body = Optional.of(body);
    }

    public static RequestBody empty() {
        return new RequestBody(Optional.empty());
    }

    public boolean isEmpty() {
        return body.isEmpty();
    }

    public String getBodyValue() {
        if (body.isEmpty()) {
            throw new IllegalArgumentException("Body가 존재하지 않습니다.");
        }
        return body.get();
    }
}
