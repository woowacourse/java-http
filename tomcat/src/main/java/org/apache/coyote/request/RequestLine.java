package org.apache.coyote.request;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.Protocol;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final RequestUrl requestUrl;
    private final Protocol protocol;

    public RequestLine(HttpMethod httpMethod, RequestUrl requestUrl, Protocol protocol) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.protocol = protocol;
    }

    public boolean isSamePath(String otherPath) {
        return requestUrl.isSamePath(otherPath);
    }

    public String getQueryValue(String key) {
        return requestUrl.getQueryValue(key);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return requestUrl.getPath();
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Map<String, String> getQueryString() {
        return requestUrl.getQueryString();
    }
}
