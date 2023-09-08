package org.apache.coyote.http11.httprequest;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpPath;
import org.apache.coyote.http11.HttpVersion;

public class HttpRequestBuilder {

    private HttpMethod method;
    private HttpPath path;
    private HttpVersion version;
    private Map<String, String> queryParameters;
    private String messageBody;
    private Map<String, String> headers;
    private Map<String, String> cookie;

    HttpRequestBuilder() {
    }

    public HttpRequestBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpRequestBuilder path(HttpPath path) {
        this.path = path;
        return this;
    }

    public HttpRequestBuilder version(HttpVersion version) {
        this.version = version;
        return this;
    }

    public HttpRequestBuilder queryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    public HttpRequestBuilder messageBody(String messageBody) {
        this.messageBody = messageBody;
        return this;
    }

    public HttpRequestBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequestBuilder cookie(Map<String, String> cookie) {
        this.cookie = cookie;
        return this;
    }

    public HttpRequest build() {
        return new HttpRequest(method, path, version, queryParameters, headers, messageBody, cookie);
    }

}
