package nextstep.jwp.response;

import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers;

    private ResponseHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders from(final Map<String, String> headers) {
        return new ResponseHeaders(headers);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
