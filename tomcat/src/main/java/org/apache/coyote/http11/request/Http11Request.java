package org.apache.coyote.http11.request;

import java.util.Map;

public class Http11Request {
    private final String method;
    private final String url;
    private final Map<String, String> header;
    private final String body;

    public Http11Request(String method, String url, Map<String, String> header, String body) {
        this.method = method;
        this.url = url;
        this.header = header;
        this.body = body;
    }

    public boolean isResource() {
        return url.endsWith(".html") || url.endsWith(".css") || url.endsWith(".js");
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Http11Request{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", header=" + header +
                ", body='" + body + '\'' +
                '}';
    }
}
