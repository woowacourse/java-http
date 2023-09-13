package org.apache.coyote.http11.response;

import java.util.Optional;

public class ResponseBody {

    private final String value;

    public ResponseBody(String value) {
        this.value = value;
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }
}
