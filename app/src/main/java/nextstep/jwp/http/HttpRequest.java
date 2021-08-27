package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String uri;
    private final Map<String, String> headers = new HashMap<>();
    private String payload;

    public HttpRequest(String method, String uri) {
        this.method = method;
        if (uri == null)
            throw new IllegalStateException("uri is null");
        this.uri = uri;
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String method() {
        return method;
    }

    public String uri() {
        return uri;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public String payload() {
        return payload;
    }
}
