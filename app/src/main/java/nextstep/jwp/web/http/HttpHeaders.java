package nextstep.jwp.web.http;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.web.http.request.HttpRequestHeaderValues;
import nextstep.jwp.web.http.response.ContentType;
import nextstep.jwp.web.http.session.HttpCookie;

public class HttpHeaders {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String LOCATION = "Location";
    private static final String COOKIE = "Cookie";
    private static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, HttpRequestHeaderValues> headers;

    public HttpHeaders() {
        this(new LinkedHashMap<>());
    }

    public HttpHeaders(
        Map<String, HttpRequestHeaderValues> headers
    ) {
        this.headers = headers;
    }

    public void add(String key, String value) {
        add(key, Collections.singletonList(value));
    }

    public void add(String key, List<String> values) {
        addValueIfKeyPresent(key, values);
        addValueIfKeyAbsent(key, values);
    }

    public void addAll(HttpHeaders headers) {
        headers.addAll(headers);
    }

    public void set(String key, String... value) {
        headers.put(key, new HttpRequestHeaderValues(value));
    }

    public int contentLength() {
        String contentLength = get(CONTENT_LENGTH).toValuesString();
        try {
            return Integer.parseInt(contentLength);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setContentLength(int contentLength) {
        set(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setContentType(ContentType contentType) {
        set(CONTENT_TYPE, contentType.getMimeType());
    }

    public HttpCookie getCookie() {
        String rawCookie = get(COOKIE).toValuesString();
        return new HttpCookie(rawCookie);
    }

    public void setLocation(String url) {
        set(LOCATION, url);
    }

    public void setResponseCookie(HttpCookie cookie) {
        set(SET_COOKIE, cookie.asString());
    }

    public Map<String, HttpRequestHeaderValues> map() {
        return headers;
    }

    private void addValueIfKeyPresent(String key, List<String> values) {
        headers.computeIfPresent(key,
            (k, v) -> v.add(values)
        );
    }

    private HttpRequestHeaderValues addValueIfKeyAbsent(String key, List<String> values) {
        return headers.computeIfAbsent(key,
            v -> new HttpRequestHeaderValues(values));
    }

    public String toValuesString(String key) {
        return headers.get(key).toValuesString();
    }

    public HttpRequestHeaderValues get(String key) {
        return headers.getOrDefault(key, new HttpRequestHeaderValues());
    }
}
