package nextstep.jwp.http.response;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseHeaders {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID_WITH_DELIMITER = "JSESSIONID=";

    private final Map<String, String> headers;

    public ResponseHeaders() {
        this(new ConcurrentHashMap<>());
    }

    private ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setContentType(String contentType) {
        headers.put(CONTENT_TYPE, contentType);
    }

    public void setContentLength(int contentLength) {
        headers.put(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setCookie(String id) {
        headers.put(SET_COOKIE, JSESSIONID_WITH_DELIMITER + id);
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
