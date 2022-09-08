package nextstep.jwp.controller;

import static org.apache.coyote.response.StatusCode.OK;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final String requestPath = request.getRequestPath();
        return HttpResponse.of(OK, ContentType.from(requestPath), requestPath);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return doPost(request);
    }
}
