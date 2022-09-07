package nextstep.jwp.model;

import java.util.Map;

public class RequestHeader {

    private Map<String, String> headers;

    private RequestHeader() {
    }

    public RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public String get(final String key) {
        return headers.get(key);
    }
}
