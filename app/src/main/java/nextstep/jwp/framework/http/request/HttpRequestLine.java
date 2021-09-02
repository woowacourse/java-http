package nextstep.jwp.framework.http.request;

import java.net.URL;
import nextstep.jwp.framework.http.common.HttpMethod;
import nextstep.jwp.framework.http.common.HttpPath;
import nextstep.jwp.framework.http.common.HttpStatus;
import nextstep.jwp.framework.http.common.ProtocolVersion;
import nextstep.jwp.framework.http.httpStatusState.HttpNotFoundStatus;
import nextstep.jwp.framework.http.httpStatusState.HttpOKStatus;
import nextstep.jwp.framework.http.httpStatusState.HttpStatusState;

public class HttpRequestLine {

    private final HttpMethod method;
    private final HttpPath path;
    private final ProtocolVersion protocolVersion;

    public HttpRequestLine(final HttpMethod method, final HttpPath path, final ProtocolVersion protocolVersion) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public URL url(final HttpStatus status) {
        if (path.isNotExistFile()) {
            return new HttpNotFoundStatus(status, path).resource();
        }

        final HttpStatusState state = new HttpOKStatus(status, path).state();
        return state.resource();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpPath path() {
        return path;
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    public boolean isPost() {
        return method.isPost();
    }

    public boolean isGet() {
        return method.isGet();
    }

    public boolean isNotPost() {
        return !method.isPost();
    }
}
