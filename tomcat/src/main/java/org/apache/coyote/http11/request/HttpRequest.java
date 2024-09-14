package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final HttpRequestHeader header;
    private final Map<String, List<String>> body;

    public HttpRequest(String method, String path,
                       HttpRequestHeader header, Map<String, List<String>> body) {
        this.method = method;
        this.path = path;
        this.header = header;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getCookies() {
        return header.getCookies();
    }

    public Map<String, List<String>> getQueryParams() {
        return header.getQueryParams();
    }

    public String getFileType() {
        return header.getFileType();
    }

    public Map<String, List<String>> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", header=" + header +
                ", body=" + body +
                '}';
    }
}
