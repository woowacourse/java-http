package org.apache.coyote.http11.response;

import java.util.Map;

public class ResponseHeader {

    private final Map<String, String> header;

    public ResponseHeader(final Map<String, String> header) {
        this.header = header;
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public Map<String, String> getHeader() {
        return header;
    }
}
