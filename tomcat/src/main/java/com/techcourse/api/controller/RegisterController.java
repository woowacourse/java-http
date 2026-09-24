package com.techcourse.api.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_PAGE = "/register.html";
    private static final String INDEX_PAGE = "/index.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.setStatus(HttpStatus.OK);
        response.setContentType(ContentType.HTML);
        response.setBody(HttpResponse.resolveResource(REGISTER_PAGE));
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        final Map<String, String> parameters = parseRequestBody(request);
        final User user = new User(
                parameters.get("account"),
                parameters.get("password"),
                parameters.get("email")
        );

        InMemoryUserRepository.save(user);
        log.info("회원가입 성공: {}", user);

        response.setStatus(HttpStatus.OK);
        response.setContentType(ContentType.HTML);
        response.setBody(HttpResponse.resolveResource(INDEX_PAGE));
    }

}
