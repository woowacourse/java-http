package nextstep.jwp.http.request;

import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getContentLength() {
        if (headers.containsKey("Content-Length")) {
            String value = headers.get("Content-Length");
            return Integer.parseInt(value);
        }
        return 0;
    }
}
