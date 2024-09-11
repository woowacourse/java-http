package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> cookies;
    private final Map<String, List<String>> queryParams;
    private final String fileType;
    private final Map<String, List<String>> body;

    public HttpRequest(String method, String path, Map<String, String> cookies,
                       Map<String, List<String>> queryParams, String fileType, Map<String, List<String>> body) {
        this.method = method;
        this.path = path;
        this.cookies = cookies;
        this.queryParams = queryParams;
        this.fileType = fileType;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public String getFileType() {
        return fileType;
    }

    public Map<String, List<String>> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", cookies=" + cookies +
                ", queryParams=" + queryParams +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
