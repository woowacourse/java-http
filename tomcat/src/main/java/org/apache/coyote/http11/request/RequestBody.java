package org.apache.coyote.http11.request;

import java.util.Optional;

public class RequestBody {
    private final QueryParameters parameters;

    public RequestBody(QueryParameters parameters) {
        this.parameters = parameters;
    }

    public static RequestBody from(final String rawBody) {
        return new RequestBody(QueryParameters.from(rawBody));
    }

    public boolean hasParameters() {
        return !parameters.isEmpty();
    }

    public Optional<String> getParameter(final String name) {
        return parameters.get(name);
    }
}
