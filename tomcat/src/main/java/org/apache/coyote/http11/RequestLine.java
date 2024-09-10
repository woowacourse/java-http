package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String requestUrl;
    private final String protocol;

    public RequestLine(String method, String requestUrl, String protocol) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.protocol = protocol;
    }

    public boolean isGet() {
        return method.equals("GET");
    }

    public boolean isRoot() {
        return requestUrl.equals("/");
    }

    public boolean isIndex() {
        return requestUrl.equals("/index.html");
    }

    public boolean hasCss() {
        return requestUrl.contains(".css");
    }

    public boolean hasJs() {
        return requestUrl.contains(".js");
    }

    public boolean hasLogin() {
        return requestUrl.contains("login");
    }

    public boolean hasQuestion() {
        return requestUrl.contains("?");
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getProtocol() {
        return protocol;
    }
}
