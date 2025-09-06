package org.apache.coyote.http.request;

public class RequestLine {

    private final String method;
    private final RequestPath path;
    private final String protocol;

    public RequestLine(String method, RequestPath path, String protocol) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
    }

    public String getMethod() {
        return method;
    }

    public String getParameter(String name) {
        return path.getParameter(name);
    }

    public String getPath(){
        return path.getPath();
    }
}
