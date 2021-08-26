package nextstep.jwp.http.request;

import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public int getContentLength() {
        if (headers.containsKey("Content-Length")) {
            String value = headers.get("Content-Length");
            value = value.trim();
            return Integer.parseInt(value);
        }
        return 0;
    }
}
