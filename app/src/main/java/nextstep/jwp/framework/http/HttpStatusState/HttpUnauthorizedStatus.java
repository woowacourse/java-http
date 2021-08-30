package nextstep.jwp.framework.http.HttpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.HttpPath;
import nextstep.jwp.framework.http.HttpStatus;

public class HttpUnauthorizedStatus implements HttpStatusState {

    @Override
    public HttpStatusState state(HttpStatus httpStatus) {
        if (httpStatus == HttpStatus.UNAUTHORIZED) {
            return this;
        }
        return new HttpNotFoundStatus().state(httpStatus);
    }

    @Override
    public URL resource() {
        return HttpPath.unAuthorized();
    }
}
