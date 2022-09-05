package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> values;

    public HttpRequestBody(String requestBody) {
        this.values = RequestParser.parseQueryString(requestBody);
    }

    public String getBodyValue(String key) {
        return values.get(key);
    }
}
