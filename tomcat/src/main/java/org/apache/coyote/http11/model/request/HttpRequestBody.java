package org.apache.coyote.http11.model.request;

import java.util.Map;

import org.apache.coyote.http11.model.RequestParser;

public class HttpRequestBody {

    private final Map<String, String> values;

    public HttpRequestBody(String requestBody) {
        this.values = RequestParser.parseQueryString(requestBody);
    }

    public String getBodyValue(String key) {
        return values.get(key);
    }
}
