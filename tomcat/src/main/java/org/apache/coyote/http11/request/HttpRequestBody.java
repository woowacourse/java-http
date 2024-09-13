package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> requestBody;

    public HttpRequestBody(Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    public String[] getKeys() {
        return requestBody.keySet().toArray(new String[0]);
    }

    public String getValue(String key) {
        return requestBody.get(key);
    }
}
