package nextstep.jwp.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private static final String HEADER_DELIMITER = ":";

    private Map<String, String> httpHeaders;

    public HttpHeader(Map<String, String> httpHeaders) {
        this.httpHeaders = new HashMap<>(httpHeaders);
    }

    public static HttpHeader getHTMLResponseHeader(String responseBody) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
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
