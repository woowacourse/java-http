package nextstep.jwp.framework.http;

import java.net.URL;

public class HttpRequestLine {

    private final HttpMethod method;
    private final HttpPath path;
    private final ProtocolVersion protocolVersion;

    public HttpRequestLine(HttpMethod method, HttpPath path, ProtocolVersion protocolVersion) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public URL getURL() {
        return path.findResourceURL();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpPath getPath() {
        return path;
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    public boolean isPost() {
        return method.isPost();
    }

}
