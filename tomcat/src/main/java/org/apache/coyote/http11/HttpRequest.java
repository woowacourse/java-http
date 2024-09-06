package org.apache.coyote.http11;

import java.util.Collections;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String url;
    private String httpVersion;
    private Map<String, String> headers;
    private Map<String, String> queries; // TODO refactor
    private String body;

    public HttpRequest(String method, String url, String httpVersion, Map<String, String> headers,
                       Map<String, String> queries, String body) {
        this.method = method;
        this.url = url;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.queries = queries;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", uri='" + url + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                ", headers=" + headers +
                ", queries=" + queries +
                ", body='" + body + '\'' +
                '}';
    }
}
