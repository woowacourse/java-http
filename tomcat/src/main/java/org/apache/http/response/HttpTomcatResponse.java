package org.apache.http.response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpTomcatResponse implements HttpResponse {

    private int statusCode = 200;
    private final Map<String, String> headers = new HashMap<>();
    private String body = "";

    @Override
    public int getStatus() {
        return statusCode;
    }

    @Override
    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }
}
