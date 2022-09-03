package nextstep.jwp.http.common;

import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public void add(String key, String value) {
        headers.put(key, value);
    }

    public int getContentLength() {
        String contentLength = headers.getOrDefault(headers.get("Content-Length"), "0");
        return Integer.parseInt(contentLength);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }
}
