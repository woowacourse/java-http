package nextstep.jwp.controller;

import static org.apache.coyote.response.StatusCode.OK;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String requestPath = request.getRequestPath();

        response.setResponse(OK, ContentType.from(requestPath), requestPath);

        response.print();
    }
}
