package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeader {

    private final Map<String, String> headers;

    public HttpHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeader from(final List<String> input) {
        Map<String, String> headers = new HashMap<>();
        for (String s : input) {
            String[] split = s.split(" ");
            String key = split[0].substring(0, split[0].lastIndexOf(":"));
            headers.put(key, split[1].trim());
        }
        return new HttpHeader(headers);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }
}
