package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        final RequestMethod method = request.getMethod();

        if (method.equals(RequestMethod.GET)) {
            return doGet(request);
        }
        if (method.equals(RequestMethod.POST)) {
            return doPost(request);
        }
        return badRequest();
    }

    protected HttpResponse doPost(HttpRequest request) {
        return badRequest();
    }

    protected HttpResponse doGet(HttpRequest request) {
        return badRequest();
    }

    private HttpResponse badRequest() {
        return new HttpResponse()
                .status(HttpStatus.BAD_REQUEST)
                .body("404.html");
    }
}