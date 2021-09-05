package nextstep.jwp.framework.http.httpstatus;

import java.net.URL;
import nextstep.jwp.framework.http.common.HttpPath;
import nextstep.jwp.framework.http.common.HttpStatus;

public abstract class HttpStatusState {

    private final HttpStatus status;
    private final HttpPath path;

    protected HttpStatusState(final HttpStatus status, final HttpPath path) {
        this.status = status;
        this.path = path;
    }

    abstract HttpStatusState state();

    public URL resource() {
        return path.findResourceURL();
    }

    protected HttpStatus getStatus() {
        return status;
    }

    protected HttpPath getPath() {
        return path;
    }

    public abstract HttpStatus status();
}
