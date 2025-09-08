package org.apache.coyote.http11.httpRequest;

import java.util.Map;

public class RequestBody {

    private final Params params;

    public RequestBody(
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

    public Map<String, String> getParams() {
        return this.params.getParams();
    }
}
