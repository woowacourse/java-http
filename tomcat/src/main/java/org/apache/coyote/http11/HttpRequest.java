package org.apache.coyote.http11;

import java.util.Collections;
import java.util.Map;

public class HttpRequest {

    private final String httpMethod;
    private final String uri;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String httpMethod, String uri, Map<String, String> queryParams, Map<String, String> headers,
                       String body) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.queryParams = queryParams;
        this.headers = headers;
        this.body = body;
    }

    public HttpCookie getCookies() {
        String cookies = headers.getOrDefault("Cookie", "");
        return new HttpCookie(cookies);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return uri.split("\\?")[0];
    }

    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
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
                "httpMethod='" + httpMethod + '\'' +
                ", uri='" + uri + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
