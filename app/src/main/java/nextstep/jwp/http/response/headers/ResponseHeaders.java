package nextstep.jwp.http.response.headers;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders() {
        this(new HashMap<>());
    }

    public ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
