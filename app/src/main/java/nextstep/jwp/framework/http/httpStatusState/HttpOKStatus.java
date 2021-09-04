package nextstep.jwp.framework.http.httpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.common.HttpPath;
import nextstep.jwp.framework.http.common.HttpStatus;

public class HttpOKStatus extends HttpStatusState {

    public HttpOKStatus(final HttpStatus status, final HttpPath path) {
        super(status, path);
    }

    @Override
    public HttpStatusState state() {
        if (getStatus() == HttpStatus.OK) {
            return this;
        }

        return new HttpCreatedStatus(getStatus(), getPath()).state();
    }

    @Override
    public URL resource() {
        return getPath().findResourceURL();
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.OK;
    }
}
