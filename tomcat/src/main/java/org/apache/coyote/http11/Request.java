package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final String uri;
    private final String protocol;
    private final Map<String, String> headers;
    private final Map<String, String> parameters = new HashMap<>();
    private final String body;

    public Request(String method, String uri, String protocol, String[] headers, String body) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
        this.headers = parseHeaders(headers);
        this.body = body;
    }

    private Map<String, String> parseHeaders(String[] headers) {
        Map<String, String> result = new HashMap<>();
        for (String header : headers) {
            String[] token = header.split(": ");
            result.put(token[0], token[1]);
        }
        return result;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
               "method='" + method + '\'' +
               ", uri='" + uri + '\'' +
               ", protocol='" + protocol + '\'' +
               ", headers=" + headers +
               ", parameters=" + parameters +
               ", body='" + body + '\'' +
               '}';
    }
}
