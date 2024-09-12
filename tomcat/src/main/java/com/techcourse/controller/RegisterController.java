package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.MimeType;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.StaticResourceProvider;
import org.apache.coyote.http11.AbstractController;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    private final InMemoryUserRepository inMemoryUserRepository = InMemoryUserRepository.getInstance();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String registerHtml = StaticResourceProvider.getStaticResource("/register.html");
        response.setBody(registerHtml)
                .setContentType(MimeType.HTML.getMimeType());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        if (!request.hasParameter(ACCOUNT) || !request.hasParameter(PASSWORD) || !request.hasParameter(EMAIL)) {
            response.badRequest();
            return;
        }

        inMemoryUserRepository.save(new User(
                request.getParameter(ACCOUNT),
                request.getParameter(PASSWORD),
                request.getParameter(EMAIL)
        ));

        response.found("/index.html");
    }
}
