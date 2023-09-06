package org.apache.coyote.request;

import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.Protocol;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final String path;
    private final Protocol protocol;
    private final Map<String, String> queryString;

    public RequestLine(HttpMethod httpMethod, String path, Protocol protocol, Map<String, String> queryString) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.protocol = protocol;
        this.queryString = queryString;
    }

    public boolean isSamePath(String urlPath) {
        return Objects.equals(this.path, urlPath);
    }

    public String getQueryValue(String key) {
        if (!queryString.containsKey(key)) {
            throw new IllegalArgumentException("키가 존재하지 않습니다.");
        }
        return queryString.get(key);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }
}
