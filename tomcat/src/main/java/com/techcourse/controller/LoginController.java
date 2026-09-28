package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        final Map<String, String> messageBody = request.getParseBodyQuery();
        final Session session = request.session();
        final boolean hasLoginSucceeded = loginAndRetrieveUserInfo(messageBody, session);
        if (hasLoginSucceeded) {
            response.sendRedirectResponse("/index.html");
            return;
        }
        response.sendRedirectResponse("/401.html");
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final Session session = request.session();
        if (session.getAttribute("user") != null) {
            response.sendRedirectResponse("/index.html");
            return;
        }
        response.sendForwardResponse(HttpStatusCode.OK, "/login.html");
    }

    private boolean loginAndRetrieveUserInfo(final Map<String, String> loginInfoPairs, final Session session) {
        String account = loginInfoPairs.getOrDefault("account", "");
        String password = loginInfoPairs.getOrDefault("password", "");

        if (!account.isBlank() && !password.isBlank()) {
            Optional<User> retrieveResult = InMemoryUserRepository.findByAccount(account);
            if (retrieveResult.isEmpty()) {
                return false;
            }
            final User retrievedUser = retrieveResult.get();
            if (retrievedUser.checkPassword(password)) {
                session.setAttribute("user", retrievedUser);
                log.info("로그인 성공! 아이디 : {}", retrievedUser.getAccount());
                log.info("User : {}", retrievedUser);
                return true;
            }
        }

        return false;
    }
}
