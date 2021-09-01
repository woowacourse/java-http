package nextstep.jwp.http.response;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders() {
        this(new ConcurrentHashMap<>());
    }

    private ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }

    public void setContentLength(int contentLength) {
        headers.put("Content-Length", String.valueOf(contentLength));
    }

    public Set<String> getKeySet() {
        return headers.keySet();
    }

    public String getValue(String key) {
        return headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
