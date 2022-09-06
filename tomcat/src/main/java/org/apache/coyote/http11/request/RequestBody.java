package org.apache.coyote.http11.request;

public class RequestBody {

    private final Params params;

    public RequestBody(final Params params) {
        this.params = params;
    }

    public static RequestBody parse(final String body) {
        return new RequestBody(Params.parse(body));
    }

    public Params getParams() {
        return params;
    }
}
