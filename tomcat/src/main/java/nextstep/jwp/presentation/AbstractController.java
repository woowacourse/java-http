package nextstep.jwp.presentation;

import static nextstep.jwp.exception.ControllerExceptionType.UNSUPPORTED_REQUEST;
import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;

import nextstep.jwp.exception.ControllerException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.method() == POST) {
            return doPost(request);
        }
        if (request.method() == GET) {
            return doGet(request);
        }
        throw new ControllerException(UNSUPPORTED_REQUEST);
    }

    protected HttpResponse doPost(HttpRequest request) {
        throw new ControllerException(UNSUPPORTED_REQUEST);
    }

    protected HttpResponse doGet(HttpRequest request) {
        throw new ControllerException(UNSUPPORTED_REQUEST);
    }
}
