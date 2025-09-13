package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;
import org.apache.coyote.http11.model.StatusCode;
import org.apache.coyote.http11.util.StaticResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String body = StaticResourceUtil.getStaticResource(request.getPath());
        response.setStatusCode(StatusCode.OK);
        response.setBodyAndContentLength(body);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final var account = request.getQueryParameter("account");
        final var password = request.getQueryParameter("password");
        final var email = request.getQueryParameter("email");

        final var user = new User(2L, account, password, email);
        InMemoryUserRepository.save(user);

        log.info("user : {}", user);
        response.setStatusCode(StatusCode.FOUND);
        response.setBodyAndContentLength(StaticResourceUtil.getStaticResource("/index.html"));
    }
}
