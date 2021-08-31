package nextstep.jwp.framework.http.HttpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.HttpPath;
import nextstep.jwp.framework.http.HttpStatus;

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
}
