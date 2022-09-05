package nextstep.jwp.http.common;

import java.util.Map;

public class HttpHeaders {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LOCATION = "Location";

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public void add(String key, String value) {
        headers.put(key, value);
    }

    public int getContentLength() {
        String contentLength = headers.getOrDefault(CONTENT_LENGTH, "0");
        return Integer.parseInt(contentLength);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }
}
