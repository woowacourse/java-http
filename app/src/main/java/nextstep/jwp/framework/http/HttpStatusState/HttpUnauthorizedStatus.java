package nextstep.jwp.framework.http.HttpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.HttpPath;
import nextstep.jwp.framework.http.HttpStatus;

public class HttpUnauthorizedStatus extends HttpStatusState {

    public HttpUnauthorizedStatus(final HttpStatus status, final HttpPath path) {
        super(status, path);
    }

    @Override
    public HttpStatusState state() {
        if (getStatus() == HttpStatus.UNAUTHORIZED) {
            return this;
        }
        return new HttpNotFoundStatus(getStatus(), getPath());
    }

    @Override
    public URL resource() {
        return HttpPath.unAuthorized();
    }
}
