package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final HttpHeaders headers;
    private final Map<String, HttpCookie> cookies = new HashMap<>();
    private HttpMethod httpMethod;
    private String uri;
    private String path;
    private Map<String, String> parameters = new HashMap<>();
    private String protocol;
    private Session session;
    private String body;

    public HttpRequest() {
        this.headers = new HttpHeaders();
    }

    public void addCookie(HttpCookie cookie) {
        this.cookies.put(cookie.getName(), cookie);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpCookie getCookie(String name) {
        return cookies.get(name);
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
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

    public boolean hasCookie(String name) {
        return this.cookies.containsKey(name);
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

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
