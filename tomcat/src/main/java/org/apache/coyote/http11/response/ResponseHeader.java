package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseHeader {
    private final Map<String, String> responseHeader;

    public ResponseHeader() {
        this.responseHeader = new ConcurrentHashMap<>();
    }

    public void setFieldValue(String field, String value) {
        responseHeader.put(field, value);
    }

    public Map<String, String> getResponseHeader() {
        return responseHeader;
    }
}
