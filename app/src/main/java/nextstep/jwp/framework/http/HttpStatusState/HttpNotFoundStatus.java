package nextstep.jwp.framework.http.HttpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.HttpPath;
import nextstep.jwp.framework.http.HttpStatus;

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
