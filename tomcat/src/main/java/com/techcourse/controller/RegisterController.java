package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String LOGIN_PAGE = "/login.html";
    private static final String REGISTER_PAGE = "/register.html";

    private static final String ACCOUNT_FIELD = "account";
    private static final String PASSWORD_FIELD = "password";
    private static final String EMAIL_FIELD = "email";

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User user = createUserFromRequest(request);
        validateUserDoesNotExist(user);
        saveUser(user);
        response.sendRedirect(LOGIN_PAGE);
    }

    private User createUserFromRequest(HttpRequest request) {
        String account = getRequiredParameter(request, ACCOUNT_FIELD);
        String password = getRequiredParameter(request, PASSWORD_FIELD);
        String email = request.getParameter(EMAIL_FIELD);
        return new User(account, password, email);
    }

    private void validateUserDoesNotExist(User user) {
        boolean userExists = InMemoryUserRepository.findByAccount(user.getAccount()).isPresent();
        if (userExists) {
            throw new IllegalArgumentException("사용자가 이미 존재합니다.");
        }
    }

    private void saveUser(User user) {
        InMemoryUserRepository.save(user);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setBody(readStaticResource(REGISTER_PAGE));
    }
}
