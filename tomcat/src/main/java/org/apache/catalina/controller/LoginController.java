package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.AbstractController;
import org.apache.catalina.Session;
import org.apache.catalina.resource.StaticResourceService;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public final class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final StaticResourceService resources = new StaticResourceService();
    private final UserSessionService userSessions = new UserSessionService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String sessionId = request.getCookie(HttpCookie.JSESSION_ID).orElseThrow();
        if (userSessions.findUser(sessionId).isPresent()) {
            response.sendRedirect("/index.html");
            return;
        }
        resources.serve("/login.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Optional<User> loginUser = login(request);
        if (loginUser.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        User user = loginUser.get();
        log.info("회원 조회 성공: account={}", user.getAccount());

        String sessionId = request.getCookie(HttpCookie.JSESSION_ID).orElseThrow();
        Session session = userSessions.getOrCreate(sessionId);
        session.setAttribute(UserSessionService.SESSION_USER, user);
        if (!session.getId().equals(sessionId)) {
            response.setCookie(HttpCookie.JSESSION_ID, session.getId());
        }
        response.sendRedirect("/index.html");
    }

    private Optional<User> login(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
