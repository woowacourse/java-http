package org.apache.coyote.http11.model;

import java.util.Map;

public class HttpRequestBody {

    private final Map<String, String> params;

    private HttpRequestBody(Map<String, String> bodyParams) {
        this.params = bodyParams;
    }

    public static HttpRequestBody from(Map<String, String> bodyParams) {
        return new HttpRequestBody(bodyParams);
    }

    public String getParam(String name) {
        return params.get(name);
    }
}
