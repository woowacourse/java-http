package nextstep.jwp.framework.http;

import java.net.URL;
import nextstep.jwp.framework.http.HttpStatusState.HttpNotFoundStatus;
import nextstep.jwp.framework.http.HttpStatusState.HttpOKStatus;
import nextstep.jwp.framework.http.HttpStatusState.HttpStatusState;

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
            return new HttpNotFoundStatus(httpStatus, path).resource();
        }

        final HttpStatusState state = new HttpOKStatus(httpStatus, path).state();
        return state.resource();
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

    public boolean isNotPost() {
        return !method.isPost();
    }
}
