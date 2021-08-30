package nextstep.jwp.framework.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {

    // General Header
    public static final String CONNECTION = "Connection";
    public static final String DATE = "Date";

    // Request Header
    public static final String AUTHORIZATION = "Authorization";
    public static final String COOKIE = "Cookie";
    public static final String USER_AGENT = "User-Agent";
    public static final String HOST = "Host";
    public static final String REFERER = "Referer";
    public static final String ORIGIN = "Origin";

    // Entity Header,
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";

    // Response Header,
    public static final String LOCATION = "Location";
    public static final String SERVER = "Server";
    public static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> headers;

    public HttpHeaders(HttpHeaders httpHeaders) {
        this(httpHeaders.headers);
    }

    public HttpHeaders() {
        this(new LinkedHashMap<>());
    }

    public HttpHeaders(Map<String, String> httpHeaders) {
        this.headers = new LinkedHashMap<>(httpHeaders);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String get(String name) {
        return headers.get(name);
    }

    public HttpHeaders addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HttpHeaders addAll(HttpHeaders headers) {
        this.headers.putAll(headers.headers);
        return this;
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }

    public boolean contains(String name) {
        return headers.containsKey(name);
    }
}
