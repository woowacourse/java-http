package nextstep.jwp.framework.http.HttpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.HttpPath;
import nextstep.jwp.framework.http.HttpStatus;

public class HttpOKStatus implements HttpStatusState {

    @Override
    public HttpStatusState state(HttpStatus httpStatus) {
        if (httpStatus == HttpStatus.OK) {
            return this;
        }

        return new HttpFoundStatus().state(httpStatus);
    }

    @Override
    public URL resource() {
        return HttpPath.index();
    }
}
