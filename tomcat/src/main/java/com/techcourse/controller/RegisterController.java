package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.resource.StaticResourceController;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final StaticResourceController staticResourceController;

    public RegisterController(final StaticResourceController staticResourceController) {
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected void doGet(
            final Http11Request request,
            final Http11Response response
    ) throws Exception {
        try {
            staticResourceController.service(request, response);
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
    }

    @Override
    protected void doPost(
            final Http11Request httpRequest,
            final Http11Response httpResponse
    ) {
        final var params = extractFirstParamValues(RequestLine.parseUrlEncodedParams(httpRequest.getBody()));
        final var user = new User(
                params.get("account"),
                params.get("password"),
                params.get("email")
        );
        InMemoryUserRepository.save(user);
        log.info("user created: {}", user);
        httpResponse.setStatus(302);
        httpResponse.setHeader("Location", "/index.html");
    }
}
