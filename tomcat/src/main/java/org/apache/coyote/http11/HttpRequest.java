package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private HttpMethod httpMethod;
    private String uri;
    private String path;
    private Map<String, String> parameters = new HashMap<>();
    private String protocol;
    private HttpHeaders headers;
    private String body;

    public HttpRequest() {
        this.headers = new HttpHeaders();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParameter(String parameterName) {
        return this.parameters.get(parameterName);
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void addHeader(String headerName, String value) {
        this.headers.put(headerName, value);
    }

    public String getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    public boolean containsHeader(String headerName) {
        return this.headers.containsHeader(headerName);
    }

    public void addParameter(String parameterName, String value) {
        this.parameters.put(parameterName, value);
    }

    public void addParameters(Map<String, String> parameters) {
        this.parameters.putAll(parameters);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
