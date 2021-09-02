package nextstep.jwp.infrastructure.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.model.web.ResourceFinder;
import nextstep.jwp.model.web.StatusCode;
import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_SUCCESS_URI = "/login.html";

    @Override
    protected void doGet(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        log.debug("Http Request - GET /register");
        String resource = ResourceFinder.resource(request.getUri() + ".html");

        response.setStatusLine(StatusCode.OK, request.getVersionOfProtocol());
        addContentHeader(response, resource.getBytes().length);
        response.setResponseBody(resource);
    }

    @Override
    protected void doPost(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        log.debug("Http Request - POST /register");
        User user = new User(
                request.getBodyValue("account"),
                request.getBodyValue("password"),
                request.getBodyValue("email")
        );
        InMemoryUserRepository.save(user);

        response.setStatusLine(StatusCode.FOUND, request.getVersionOfProtocol());
        response.forward(REGISTER_SUCCESS_URI);
    }
}
