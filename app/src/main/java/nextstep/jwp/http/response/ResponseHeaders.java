package nextstep.jwp.http.response;

import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
