package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
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

    public HttpResponse getLoginHtml(final HttpRequest httpRequest, final SessionManager sessionManager) {
        final HttpCookie cookie = httpRequest.getCookie();
        if (!cookie.containsName("JSESSIONID")) {
            return HttpResponse.ok("login.html");
        }

        final String sessionId = cookie.getByName("JSESSIONID");
        final HttpSession session = sessionManager.findSession(sessionId);
        if (session == null) {
            return HttpResponse.ok("login.html");
        }

        final User user = (User) session.getAttribute("user");
        if (user == null) {
            return HttpResponse.ok("login.html");
        }
        return HttpResponse.found("index.html");
    }

    public HttpResponse login(final String account, final String password, final SessionManager sessionManager) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다: %s".formatted(account)));

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException();
        }

        log.info("user : {}", user);
        final HttpResponse httpResponse = HttpResponse.found("index.html");
        String sessionId = UUID.randomUUID().toString();
        httpResponse.setCookie("JSESSIONID", sessionId);
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);
        return httpResponse;
    }

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
