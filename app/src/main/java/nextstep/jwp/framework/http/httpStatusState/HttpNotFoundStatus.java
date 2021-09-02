package nextstep.jwp.framework.http.httpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.common.HttpPath;
import nextstep.jwp.framework.http.common.HttpStatus;

public class HttpNotFoundStatus extends HttpStatusState {

    public HttpNotFoundStatus(final HttpStatus status, final HttpPath path) {
        super(status, path);
    }

    @Override
    public HttpStatusState state() {
        return this;
    }

    @Override
    public URL resource() {
        return HttpPath.notFound();
    }
}
