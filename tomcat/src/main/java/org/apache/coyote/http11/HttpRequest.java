package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final float version;
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String version,
                       String method,
                       String path,
                       Map<String, String> headers,
                       String body) {
        this.version = Float.parseFloat(version); // TODO: 자리수 확인하기
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public HttpCookie getCookies() {
        String cookies = headers.getOrDefault("Cookie", "");
        return new HttpCookie(cookies);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() { // TODO: test
        Map<String, String> queryParams = new HashMap<>();
        if (!path.contains("?")) {
            return queryParams;
        }
        String queryString = path.split("\\?")[1];
        String[] rawQueryParams = queryString.split("&");
        for (String rawQueryParam : rawQueryParams) {
            String key = rawQueryParam.split("=")[0];
            String value = rawQueryParam.split("=")[1];
            queryParams.put(key, value);
        }
        return queryParams;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "httpMethod='" + method + '\'' +
                ", uri='" + path + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
