package nextstep.jwp.framework.http.httpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.common.HttpPath;
import nextstep.jwp.framework.http.common.HttpStatus;

public class HttpFoundStatus extends HttpStatusState {

    public HttpFoundStatus(final HttpStatus status, final HttpPath path) {
        super(status, path);
    }

    @Override
    public HttpStatusState state() {
        if (getStatus() == HttpStatus.FOUND) {
            return this;
        }

        return new HttpUnauthorizedStatus(getStatus(), getPath()).state();
    }

    @Override
    public URL resource() {
        return HttpPath.index();
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.FOUND;
    }
}
