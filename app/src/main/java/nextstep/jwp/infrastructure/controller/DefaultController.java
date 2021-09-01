package nextstep.jwp.infrastructure.controller;

import nextstep.jwp.model.web.ResourceFinder;
import nextstep.jwp.model.web.StatusCode;
import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;

public class DefaultController extends AbstractController {

    private static final String DEFAULT_URI = "/login.html";

    @Override
    protected void doGet(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        String resource = ResourceFinder.resource(DEFAULT_URI);

        response.setStatusLine(StatusCode.OK, request.getVersionOfProtocol());
        response.setHeaders(headers(resource.getBytes().length));
        response.setResponseBody(resource);
    }
}
