package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.handler.RegisterHandler;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.StatusLine;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        renderRegisterPage(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        if (request.equalContentType("application/x-www-form-urlencoded")) {
            register(request, response);
        }
    }

    private void renderRegisterPage(final HttpRequest request, final HttpResponse response) throws IOException {
        final String body = readResource("/register.html");

        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.HTML.getMimeType());
        headers.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        response.setStatusLine(new StatusLine(request.getVersion(), StatusCode.OK));
        response.setHeaders(headers);
        response.setBody(body.getBytes(StandardCharsets.UTF_8));
    }

    private void register(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final Map<String, String> formParams = request.getFormParams();
        final String account = formParams.get("account");
        final String email = formParams.get("email");
        final String password = formParams.get("password");
        final User user = new User(account, password, email);

        if (account != null && email != null && password != null) {
            InMemoryUserRepository.save(user);
            log.info("회원가입 완료 {}", user);
            redirect(request, response, "/index.html");
        }
    }
}
