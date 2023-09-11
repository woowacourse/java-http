package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeader {

    private final Map<String, String> header;

    public ResponseHeader() {
        this.header = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public Map<String, String> getHeader() {
        return header;
    }
}
