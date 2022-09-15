package nextstep.jwp.controller;

import static org.apache.coyote.http11.HttpStatusCode.OK;
import static util.FileLoader.loadFile;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseBody;
import org.apache.coyote.http11.exception.methodnotallowed.MethodNotAllowedException;

public class StaticFileController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpResponseBody httpResponseBody = HttpResponseBody.ofFile(loadFile(request.getUrl()));
        response.statusCode(OK)
                .responseBody(httpResponseBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        throw new MethodNotAllowedException();
    }
}
