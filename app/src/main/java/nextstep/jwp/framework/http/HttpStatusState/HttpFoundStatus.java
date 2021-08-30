package nextstep.jwp.framework.http.HttpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.HttpPath;
import nextstep.jwp.framework.http.HttpStatus;

public class HttpFoundStatus implements HttpStatusState {

    @Override
    public HttpStatusState state(HttpStatus httpStatus) {
        if (httpStatus == HttpStatus.FOUND) {
            return this;
        }

        return new HttpUnauthorizedStatus().state(httpStatus);
    }

    @Override
    public URL resource() {
        return HttpPath.index();
    }
}
