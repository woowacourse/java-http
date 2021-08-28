package nextstep.jwp.framework.http;

import java.net.URL;

public class HttpRequestLine {

    private final HttpMethod method;
    private final HttpPath path;
    private final ProtocolVersion protocolVersion;

    public HttpRequestLine(final HttpMethod method, final HttpPath path, final ProtocolVersion protocolVersion) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public URL url(final HttpStatus httpStatus) {
        if (path.isNotExistFile()) {
            return HttpPath.notFound();
        }

        if (httpStatus == HttpStatus.FOUND) {
            return HttpPath.index();
        }

        if (httpStatus == HttpStatus.UNAUTHORIZED) {
            return HttpPath.unAuthorized();
        }

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
