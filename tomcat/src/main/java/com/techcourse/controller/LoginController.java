package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void get(HttpRequest request, HttpResponse response) {
        HttpCookie cookie = new HttpCookie(request.getHeader("Cookie"));
        Session session = SessionManager.getInstance().findSession(cookie.get("JSESSIONID"));
        if (session != null && session.getAttribute("user") != null) {
            redirect(response, "/index.html");
            return;
        }

        new StaticResourceController().render(request.getPath(), response);
    }

    @Override
    protected void post(HttpRequest request, HttpResponse response) {
        Session loginSession = new Session(UUID.randomUUID().toString());
        HttpStatus status = findUser(request.getParameters(), loginSession);
        if (status == HttpStatus.FOUND) {
            SessionManager.getInstance().add(loginSession);
            response.setHeader("Set-Cookie", "JSESSIONID=" + loginSession.getId());
            redirect(response, "/index.html");
            return;
        }
        if (status == HttpStatus.UNAUTHORIZED) {
            redirect(response, "/401.html");
            return;
        }

        new StaticResourceController().render(request.getPath(), response);
    }

    private void redirect(HttpResponse response, String location) {
        response.setStatus(HttpStatus.FOUND);
        response.setHeader("Location", location);
    }

    private HttpStatus findUser(Map<String, String> queryParams, Session session) {
        if (queryParams.isEmpty()) {
            return HttpStatus.OK;
        }

        final String account = queryParams.get("account");
        if (account == null || account.isBlank()) {
            log.info("아이디는 필수값입니다.");
            return HttpStatus.UNAUTHORIZED;
        }

        final String password = queryParams.get("password");
        if (password == null || password.isBlank()) {
            log.info("비밀번호는 필수값입니다.");
            return HttpStatus.UNAUTHORIZED;
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return HttpStatus.UNAUTHORIZED;
        }

        User foundUser = user.get();
        if (checkPassword(queryParams, foundUser)) {
            session.setAttribute("user", foundUser);
            log.info("user: {}", foundUser);
            return HttpStatus.FOUND;
        }

        return HttpStatus.UNAUTHORIZED;
    }

    private boolean checkPassword(Map<String, String> queryParams, User user) {
        if (!user.checkPassword(queryParams.get("password"))) {
            log.info("비밀번호가 일치하지 않습니다.");
            return false;
        }

        return true;
    }
}
