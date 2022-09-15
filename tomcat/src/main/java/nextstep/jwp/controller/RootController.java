package nextstep.jwp.controller;

import static org.apache.coyote.http11.HttpStatusCode.OK;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseBody;
import org.apache.coyote.http11.exception.methodnotallowed.MethodNotAllowedException;

public class RootController extends AbstractController {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        doGet(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final HttpResponseBody httpResponseBody = HttpResponseBody.of("Hello world!");
        response.statusCode(OK)
                .responseBody(httpResponseBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        throw new MethodNotAllowedException();
    }
}
