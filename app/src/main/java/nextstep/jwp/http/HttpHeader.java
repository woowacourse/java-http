package nextstep.jwp.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private final Map<String, String> httpHeaders;

    public HttpHeader(Map<String, String> httpHeaders) {
        this.httpHeaders = new HashMap<>(httpHeaders);
    }

    public static HttpHeader getResponseHeader(String responseBody, ContentType contentType, HttpHeader httpHeader) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType.getHeaderValue());
        headers.put("Content-Length", responseBody.getBytes().length + "");

        if (contentType == ContentType.HTML) {
            setSessionIfNotPresent(httpHeader, headers);
        }

        return new HttpHeader(headers);
    }

    private static void setSessionIfNotPresent(HttpHeader httpHeader, Map<String, String> headers) {
        HttpCookie httpCookie = HttpCookie.fromHeader(httpHeader);

        if (!httpCookie.containsSessionId()) {
            headers.putAll(HttpCookie.ofSessionCookie().toHeaderFormat());
        }
    }

    public void addAttribute(String key, String value) {
        this.httpHeaders.put(key, value);
    }

    public Map<String, String> getAllHeaders() {
        return Collections.unmodifiableMap(this.httpHeaders);
    }

    public String getValueByKey(String key) {
        return this.httpHeaders.get(key);
    }
}
