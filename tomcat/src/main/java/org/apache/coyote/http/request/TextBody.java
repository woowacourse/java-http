package org.apache.coyote.http.request;

import java.util.Optional;

public record TextBody(String raw) implements RequestBody {

    static final TextBody EMPTY = new TextBody("");

    @Override
    public Optional<String> get(String key) {
        throw new UnsupportedOperationException("지원되지 않는 연산입니다.");
    }
}
