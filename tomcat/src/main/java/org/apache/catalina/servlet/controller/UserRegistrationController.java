package org.apache.catalina.servlet.controller;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.resource.StaticResourceFinder;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class UserRegistrationController extends AbstractController {

    private static final String REGISTER_SUCCESSFUL_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String ID_BODY_NAME = "account";
    private static final String EMAIL_BODY_NAME = "email";
    private static final String PASSWORD_BODY_NAME = "password";

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final var email = request.getBodyContent(EMAIL_BODY_NAME);
        final var account = request.getBodyContent(ID_BODY_NAME);
        final var password = request.getBodyContent(PASSWORD_BODY_NAME);
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원 ID");
        }
        InMemoryUserRepository.save(new User(account, password, email));
        final HttpResponse httpResponse = StaticResourceFinder.renderRedirect(REGISTER_SUCCESSFUL_REDIRECT_URI);
        response.copyProperties(httpResponse);
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) {
        final HttpResponse httpResponse = StaticResourceFinder.render("register.html");
        response.copyProperties(httpResponse);
    }
}
