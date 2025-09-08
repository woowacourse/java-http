package org.apache.coyote.http11.httpRequest;

import java.util.Optional;

public class RequestBody {

    private final Params params;

    private RequestBody(
            final Params params
    ) {
        this.params = params;
    }

    public static RequestBody parse(final String body) {
        return new RequestBody(Params.parse(body));
    }

    public static RequestBody empty() {
        return new RequestBody(Params.empty());
    }

    public Optional<String> findParamsValue(final String name) {
        return this.params.findValue(name);
    }
}
