package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;

public abstract class AbstractController implements Controller {

    @Override
    public ResponseEntity service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.getMethod().equals("POST")) {
            return doPost(httpRequest, httpResponse);
        }
        if (httpRequest.getMethod().equals("GET")) {
            return doGet(httpRequest, httpResponse);
        }
        return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, "/404.html");
    }

    protected abstract ResponseEntity doPost(final HttpRequest httpRequest, final HttpResponse httpResponse);
    protected abstract ResponseEntity doGet(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
