package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String target;
    private final String version;

    private RequestLine(final String method, final String target, final String version) {
        this.method = method;
        this.target = target;
        this.version = version;
    }

    public static RequestLine from(final String method, final String target, final String version) {
        return new RequestLine(method, target, version);
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
