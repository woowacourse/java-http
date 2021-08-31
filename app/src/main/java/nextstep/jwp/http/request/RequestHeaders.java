package nextstep.jwp.http.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHeaders {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headersExceptCookies;
    private final RequestCookie requestCookie;

    public RequestHeaders(Map<String, String> headers) {
        headersExceptCookies = new ConcurrentHashMap<>();
        requestCookie = new RequestCookie();
        saveHeaders(headers);
    }

    private void saveHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            saveClassifiedHeaders(entry.getKey(), entry.getValue());
        }
    }

    private void saveClassifiedHeaders(String key, String value) {
        if ("Cookie".equals(key)) {
            requestCookie.add(value);
            return;
        }
        headersExceptCookies.put(key, value);
    }

    public int getContentLength() {
        if (headersExceptCookies.containsKey(CONTENT_LENGTH)) {
            String value = headersExceptCookies.get(CONTENT_LENGTH);
            return Integer.parseInt(value);
        }
        return 0;
    }

    public RequestCookie getHttpCookie() {
        return requestCookie;
    }
}
