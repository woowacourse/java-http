package nextstep.jwp.controller;

import static common.ResponseStatus.OK;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setResponseResource(OK, httpRequest.requestUri());
    }
}
