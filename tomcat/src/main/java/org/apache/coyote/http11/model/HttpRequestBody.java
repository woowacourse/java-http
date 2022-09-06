package org.apache.coyote.http11.model;

import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> params;

    public HttpRequestBody(Map<String, String> bodyParams) {
        this.params = bodyParams;
    }

    public String getParam(String name) {
        return params.get(name);
    }
}
