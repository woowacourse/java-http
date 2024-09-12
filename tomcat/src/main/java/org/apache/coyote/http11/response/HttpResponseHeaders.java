package org.apache.coyote.http11.response;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponseHeaders {

    private final Map<String, String> headers;

    public HttpResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setContentType(HttpRequest request) {
        this.headers.put("Content-Type", request.getContentType() + ";charset=utf-8");
    }

    public void setContentLength(HttpResponseBody responseBody) {
        this.headers.put("Content-Length", String.valueOf(responseBody.body().getBytes().length));
    }

    public String[] getKeys() {
        return headers.keySet().toArray(new String[0]);
    }

    public String getValue(String key) {
        return headers.get(key);
    }
}
