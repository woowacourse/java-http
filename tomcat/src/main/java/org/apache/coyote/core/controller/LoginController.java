package org.apache.coyote.core.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.manager.Session;
import org.apache.coyote.manager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws UncheckedServletException {
        super.doGet(request, response);

        if (SessionManager.hasSession(request.getCookie())) {
            response.sendRedirect("./index.html");
        }
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response)
            throws UncheckedServletException {
        super.doPost(request, response);
        Login(request, response);
    }

    private void Login(final HttpRequest request, final HttpResponse response) {
        Map<String, String> requestBodies = request.getRequestBodies();
        String account = requestBodies.get("account");
        String password = requestBodies.get("password");

        User user = findUser(account);

        if (!user.checkPassword(password)) {
            response.sendRedirect("./401.html");
            return;
        }
        registerUserCookieToSession(response, user);
    }

    private User findUser(final String account) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
        printUser(user);
        return user;
    }

    private void printUser(final User user) {
        log.info(user.toString());
    }

    private void registerUserCookieToSession(final HttpResponse response, final User user) {
        HttpCookie cookie = new HttpCookie();
        response.setCookie(cookie);
        Session session = new Session("user", user);
        SessionManager.add(cookie.getCookie(), session);
    }
}
