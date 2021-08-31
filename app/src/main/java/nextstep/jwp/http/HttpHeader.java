package nextstep.jwp.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private static final String HTML_HEADER_VALUE = "text/html;charset=utf-8";
    private static final String CSS_HEADER_VALUE = "text/css";

    private Map<String, String> httpHeaders;

    public HttpHeader(Map<String, String> httpHeaders) {
        this.httpHeaders = new HashMap<>(httpHeaders);
    }

    public static HttpHeader getHTMLResponseHeader(String responseBody) {
        return getHeader(responseBody, HTML_HEADER_VALUE);
    }

    public static HttpHeader getCSSResponseHeader(String responseBody) {
        return getHeader(responseBody, CSS_HEADER_VALUE);
    }

    private static HttpHeader getHeader(String responseBody, String contentType) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", responseBody.getBytes().length + "");

        return new HttpHeader(headers);
    }

    public Map<String, String> getAllHeaders() {
        return Collections.unmodifiableMap(this.httpHeaders);
    }

    public String getValueByKey(String key) {
        return this.httpHeaders.get(key);
    }
}
