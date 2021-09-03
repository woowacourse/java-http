package nextstep.jwp.infrastructure.controller;

import nextstep.jwp.model.web.ResourceFinder;
import nextstep.jwp.model.web.StatusCode;
import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);
    private static final String DEFAULT_URI = "/login.html";

    @Override
    protected void doGet(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        log.debug("Http Request - GET /");
        String resource = ResourceFinder.resource(DEFAULT_URI);

        response.setStatusLine(StatusCode.OK, request.getVersionOfProtocol());
        addContentHeader(response, resource.getBytes().length);
        response.setResponseBody(resource);
    }
}
