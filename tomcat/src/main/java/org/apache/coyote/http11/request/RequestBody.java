package org.apache.coyote.http11.request;

import java.util.Optional;

public class RequestBody {

    String body;

    public RequestBody(String body) {
        this.body = body;
    }

    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }
}
