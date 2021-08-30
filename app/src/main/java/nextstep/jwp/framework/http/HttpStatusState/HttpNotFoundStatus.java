package nextstep.jwp.framework.http.HttpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.HttpPath;
import nextstep.jwp.framework.http.HttpStatus;

public class HttpNotFoundStatus implements HttpStatusState {

    @Override
    public HttpStatusState state(HttpStatus httpStatus) {
        return this;
    }

    @Override
    public URL resource() {
        return HttpPath.notFound();
    }
}
