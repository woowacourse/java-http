package nextstep.jwp.framework.http.httpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.common.HttpPath;
import nextstep.jwp.framework.http.common.HttpStatus;

public class HttpUnauthorizedStatus extends HttpStatusState {

    public HttpUnauthorizedStatus(final HttpStatus status, final HttpPath path) {
        super(status, path);
    }

    @Override
    public HttpStatusState state() {
        if (getStatus() == HttpStatus.UNAUTHORIZED) {
            return this;
        }
        return new HttpNotFoundStatus(getStatus(), getPath()).state();
    }

    @Override
    public URL resource() {
        return HttpPath.unAuthorized();
    }
}
