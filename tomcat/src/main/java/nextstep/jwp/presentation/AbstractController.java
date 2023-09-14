package nextstep.jwp.presentation;

import static nextstep.jwp.exception.ControllerExceptionType.UNSUPPORTED_REQUEST;
import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;

import nextstep.jwp.exception.ControllerException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.method() == POST) {
            doPost(request, response);
            return;
        }
        if (request.method() == GET) {
            doGet(request, response);
            return;
        }
        throw new ControllerException(UNSUPPORTED_REQUEST);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }
}
