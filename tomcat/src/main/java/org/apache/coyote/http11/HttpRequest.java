package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final Map<String, String> data;
    private final QueryParameter queryParameter;

    public HttpRequest(Map<String, String> data) {
        this.data = new HashMap<>(data);
        this.queryParameter = new QueryParameter();
    }

    public HttpRequest(Map<String, String> data, QueryParameter queryParameter) {
        this.data = new HashMap<>(data);
        this.queryParameter = new QueryParameter(queryParameter);
    }

    public String getHttpMethod() {
        return data.get("method");
    }

    public String getUri() {
        return data.get("request-uri");
    }

    public String getHttpVersion() {
        return data.get("http-version");
    }

    public String getQueryParameter(String name) {
        return queryParameter.getValue(name);
    }
}
