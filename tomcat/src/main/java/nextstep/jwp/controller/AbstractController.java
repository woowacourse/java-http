package nextstep.jwp.controller;

import static org.apache.coyote.http11.response.HttpStatus.METHOD_NOT_ALLOWED;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) {
        final RequestMethod method = request.getMethod();

        if (method.equals(RequestMethod.GET)) {
            return doGet(request);
        }
        if (method.equals(RequestMethod.POST)) {
            return doPost(request);
        }
        return methodNotAllowed();
    }

    protected HttpResponse doPost(final HttpRequest request) {
        return badRequest();
    }

    protected HttpResponse doGet(final HttpRequest request) {
        return badRequest();
    }

    private HttpResponse badRequest() {
        return new HttpResponse()
                .status(HttpStatus.BAD_REQUEST)
                .body("404.html");
    }

    private HttpResponse methodNotAllowed() {
        return new HttpResponse()
                .status(METHOD_NOT_ALLOWED)
                .body("405.html");
    }
}