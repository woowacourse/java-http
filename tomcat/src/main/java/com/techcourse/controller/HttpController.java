package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpController {

    private static final Logger log = LoggerFactory.getLogger(HttpController.class);

    public HttpResponse helloWorld() {
        return HttpResponse.ok("Hello world!");
    }

    public HttpResponse getIndex() {
        return HttpResponse.ok("index.html");
    }

    public HttpResponse getCssStyles() {
        return HttpResponse.ok("css/styles.css");
    }

    public HttpResponse getScripts() {
        return HttpResponse.ok("js/scripts.js");
    }

    public HttpResponse getLoginHtml(final HttpRequest httpRequest) {
        final HttpSession session = httpRequest.getSession();
        if (session == null) {
            return HttpResponse.ok("login.html");
        }

        final Object user = session.getAttribute("user");
        if (user instanceof User) {
            return HttpResponse.found("index.html");
        }
        return HttpResponse.ok("login.html");
    }

    public HttpResponse login(final String account, final String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 유저입니다: %s".formatted(account)));

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("유효하지 않는 password입니다.: %s".formatted(user.getAccount()));
        }

        log.info("user : {}", user);
        final HttpResponse httpResponse = HttpResponse.found("index.html");
        String sessionId = UUID.randomUUID().toString();
        httpResponse.setCookie("JSESSIONID", sessionId);
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        SessionManager.INSTANCE.add(session);
        return httpResponse;
    }

    public HttpResponse getRegisterHtml() {
        return HttpResponse.ok("register.html");
    }

    public HttpResponse getRegister(final String account, final String email, final String password) {
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return HttpResponse.found("index.html");
    }

    public HttpResponse getChartArea() {
        return HttpResponse.ok("assets/chart-area.js");
    }

    public HttpResponse getChartBar() {
        return HttpResponse.ok("assets/chart-bar.js");
    }

    public HttpResponse getChartPie() {
        return HttpResponse.ok("assets/chart-pie.js");
    }
}
