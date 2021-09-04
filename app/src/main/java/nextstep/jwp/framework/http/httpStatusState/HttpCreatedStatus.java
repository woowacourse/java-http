package nextstep.jwp.framework.http.httpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.common.HttpPath;
import nextstep.jwp.framework.http.common.HttpStatus;

public class HttpCreatedStatus extends HttpStatusState {

    public HttpCreatedStatus(final HttpStatus status, final HttpPath path) {
        super(status, path);
    }

    @Override
    public HttpStatusState state() {
        if (getStatus() == HttpStatus.CREATED) {
            return this;
        }

        return new HttpFoundStatus(getStatus(), getPath()).state();
    }

    @Override
    public URL resource() {
        return HttpPath.index();
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.CREATED;
    }
}
