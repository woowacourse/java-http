package nextstep.jwp.http.request;

import java.util.Map;

public class HttpHeader {

    private final Map<String, String> headers;

    public HttpHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getContentLength() {
        if (!headers.containsKey("Content-Length")) {
            return 0;
        }
        return Integer.parseInt(headers.get("Content-Length"));
    }
}
