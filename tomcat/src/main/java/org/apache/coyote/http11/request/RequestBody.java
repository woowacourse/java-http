package org.apache.coyote.http11.request;

import java.util.Optional;

public class RequestBody {

    private final Params params;

    public RequestBody(final Params params) {
        this.params = params;
    }

    public static RequestBody parse(final String body) {
        return new RequestBody(Params.parse(body));
    }

    public Optional<String> findParam(final String name) {
        return params.find(name);
    }
}
