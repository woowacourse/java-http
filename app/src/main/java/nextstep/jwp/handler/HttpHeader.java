package nextstep.jwp.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpHeader {
    private final Map<String, String> headerMap;

    public HttpHeader(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public int getContentLength() {
        String contentLength = headerMap.getOrDefault("Content-Length", "0");
        return Integer.parseInt(contentLength);
    }

    public void addHeader(String key, String value) {
        this.headerMap.put(key, value);
    }

    public String makeHttpMessage() {
        List<String> headers = new ArrayList<>();
        for (String key : headerMap.keySet()) {
            String join = String.join(": ", key, headerMap.get(key) + " ");
            headers.add(join);
        }
        return String.join("\r\n", headers);
    }
}
