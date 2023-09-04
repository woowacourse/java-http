package org.apache.coyote.http11.request;

public class HttpRequestLine {

    private final String method;
    private final String target;
    private final String version;

    private HttpRequestLine(final String method, final String target, final String version) {
        this.method = method;
        this.target = target;
        this.version = version;
    }

    public static HttpRequestLine from(final String method, final String target, final String version) {
        return new HttpRequestLine(method, target, version);
    }

    public String getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public String getVersion() {
        return version;
    }
}
