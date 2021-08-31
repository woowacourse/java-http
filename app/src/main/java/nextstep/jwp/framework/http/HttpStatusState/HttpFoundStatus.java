package nextstep.jwp.framework.http.HttpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.HttpPath;
import nextstep.jwp.framework.http.HttpStatus;

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
}
