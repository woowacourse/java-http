package nextstep.jwp.framework.http.HttpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.HttpPath;
import nextstep.jwp.framework.http.HttpStatus;

public class HttpOKStatus extends HttpStatusState {

    public HttpOKStatus(final HttpStatus status, final HttpPath path) {
        super(status, path);
    }

    @Override
    public HttpStatusState state() {
        if (getStatus() == HttpStatus.OK) {
            return this;
        }

        return new HttpFoundStatus(getStatus(), getPath());
    }

    @Override
    public URL resource() {
        return getPath().findResourceURL();
    }
}
