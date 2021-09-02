package nextstep.jwp.infrastructure.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.web.ResourceFinder;
import nextstep.jwp.model.web.StatusCode;
import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_SUCCESS_URL = "/index.html";
    private static final String LOGIN_FAILURE_URL = "/401.html";

    @Override
    protected void doGet(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        log.debug("Http Request - GET /login");
        String resource = ResourceFinder.resource(request.getUri() + ".html");

        response.setStatusLine(StatusCode.OK, request.getVersionOfProtocol());
        response.setHeaders(headers(resource.getBytes().length));
        response.setResponseBody(resource);
    }

    @Override
    protected void doPost(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        log.debug("Http Request - POST /login");
        response.setStatusLine(StatusCode.FOUND, request.getVersionOfProtocol());

        if (InMemoryUserRepository.login(
                request.getBodyValue("account"), request.getBodyValue("password"))) {
            response.setHeaders(postHeaders(LOGIN_SUCCESS_URL));
            return;
        }
        response.setHeaders(postHeaders(LOGIN_FAILURE_URL));
    }

    private Map<String, String> postHeaders(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", url);

        return headers;
    }
}
