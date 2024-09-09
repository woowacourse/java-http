package org.apache.coyote.http11;

import java.util.Collections;
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
        this.version = Float.parseFloat(version);
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public HttpCookie extractCookie() {
        HttpCookie httpCookie = new HttpCookie();
        String rawCookies = headers.get("Cookie");
        if (rawCookies == null) {
            return httpCookie;
        }
        String[] cookies = rawCookies.split("; ");
        for (String cookie : cookies) {
            String key = cookie.split("=")[0];
            String value = cookie.split("=")[1];
            httpCookie.addCookie(key, value);
        }
        return httpCookie;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
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
