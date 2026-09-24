package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.StaticResource;
import org.apache.catalina.StaticResources;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends AbstractController {
    private static final String USER_ATTRIBUTE = "user";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.findSession().isPresent()) {
            response.setRedirect("/index.html");
            return;
        }

        Optional<StaticResource> loginPage = StaticResources.find("/login.html");
        if (loginPage.isEmpty()) {
            response.setError(HttpStatus.NOT_FOUND);
            return;
        }
        response.setBody(loginPage.get().mimeType(), loginPage.get().content());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Optional<String> account = request.getParameter("account");
        Optional<String> password = request.getParameter("password");
        if (account.isEmpty() || password.isEmpty()) {
            response.setError(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<User> loginUser = login(account.get(), password.get());
        if (loginUser.isEmpty()) {
            response.setRedirect("/401.html");
            return;
        }
        doNewLogin(request, response, loginUser.get());
    }

    private Optional<User> login(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private void doNewLogin(HttpRequest request, HttpResponse response, User user) throws IOException {
        HttpSession session = request.renewSession();
        session.setAttribute(USER_ATTRIBUTE, user);
        response.setRedirect("/index.html");
    }
}
