package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private HttpMethod method;
    private String path;
    private Map<String, String> queryProperties;
    private String protocolVersion;

    private Map<String, String> headers;

    private String body;

    public HttpRequest(final HttpMethod method, final String path, final Map<String, String> queryProperties, final String protocolVersion, final Map<String, String> headers,
                       final String body) {
        this.method = method;
        this.path = path;
        this.queryProperties = queryProperties;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
        this.body = body;

    }

    public boolean isPOST() {
        return this.method.equals(HttpMethod.POST);
    }

    public boolean isGET() {
        return this.method.equals(HttpMethod.GET);
    }

    public boolean isNotExistBody() {
        return this.body == null;
    }

    public boolean isSamePath(String path) {
        return this.path.equals(path);
    }

    public String getAccept() {
        return this.headers.get("Accept");
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(this.headers.get("Cookie"));
    }

    public String getPath() {
        return this.path;
    }

    public String getBody() {
        return this.body;
    }

}
