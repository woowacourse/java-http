package org.apache.coyote.http11;

public class HttpRequest {

    private final HttpMethod method;
    private final HttpUri uri;
    private final HttpProtocol protocol;
    private final HttpRequestHeader header;
    private final HttpCookie cookie;
    private final HttpRequestBody body;
    private final HttpQueryParameter parameter;

    public HttpRequest(HttpMethod method,
                       HttpUri uri,
                       HttpProtocol protocol,
                       HttpRequestHeader header,
                       HttpCookie cookie,
                       HttpRequestBody body,
                       HttpQueryParameter parameter) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
        this.header = header;
        this.cookie = cookie;
        this.body = body;
        this.parameter = parameter;
    }

    public void addHeader(String name, String value) {
        header.addHeader(name, value);
    }

    public void addCookie(String name, String value) {
        cookie.addCookie(name, value);
    }

    public String getResourcePath() {
        return uri.getPath();
    }

    public String getQueryParameter(String name) {
        return parameter.getValue(name);
    }

    public String getCookie(String name) {
        return cookie.getCookie(name);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpUri getUri() {
        return uri;
    }

    public HttpProtocol getHttpProtocol() {
        return protocol;
    }

    public HttpRequestHeader getHeader() {
        return header;
    }

    public HttpRequestBody getBody() {
        return body;
    }

    public HttpCookie getCookies() {
        return cookie;
    }
}
