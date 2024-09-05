package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequestHeader {

    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;

    public HttpRequestHeader(HttpMethod method, String path, String version, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
    }

    public boolean containsKey(String key) {
        return headers.containsKey(key);
    }

    public boolean isMethod(String name) {
        return method.isMethod(name);
    }

    public boolean isPath(String path) {
        return this.path.equals(path);
    }

    public String getValue(String key) {
        return headers.get(key);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "HttpRequestHeader{" +
                "\nmethod='" + method + '\'' +
                ", \npath='" + path + '\'' +
                ", \nversion='" + version + '\'' +
                ", \nheaders=" + headers +
                '}';
    }
}
