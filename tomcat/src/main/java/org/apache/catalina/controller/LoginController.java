package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.UUID;
import java.util.function.Consumer;
import org.apache.catalina.session.HttpSession;
import org.apache.catalina.session.HttpSessionManger;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final String JSESSIONID = "JSESSIONID";
    private static final HttpSessionManger HTTP_SESSION_MANGER = new HttpSessionManger();
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();

        if (method.isGet()) {
            doGet(request, response);
        }

        if (method.isPost()) {
            doPost(request, response);
        }
    }

    private void doGet(HttpRequest request, HttpResponse response) {
        HttpCookie httpCookie = request.getCookie();

        if (httpCookie.isContains(JSESSIONID) && checkAlreadyLogin(httpCookie)) {
            response.setRedirect("/index.html");
            return;
        }

        response.setRedirect("/login.html");
    }

    private boolean checkAlreadyLogin(HttpCookie httpCookie) {
        String sessionId = httpCookie.findCookie(JSESSIONID);
        HttpSession session = HTTP_SESSION_MANGER.findSession(sessionId);
        return session.getAttribute("user") != null;
    }

    private void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBody().get("account");
        String password = request.getBody().get("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        login(response, password),
                        () -> response.setRedirect("/401.html")
                );
    }

    private Consumer<User> login(HttpResponse response, String password) {
        return user -> {
            if (!user.checkPassword(password)) {
                response.setRedirect("/401.html");
                return;
            }

            String sessionId = saveSession(user);

            log.info("로그인 성공 :: account = {}", user.getAccount());
            response.setCookie(JSESSIONID, sessionId);
            response.setRedirect("/index.html");
        };
    }

    private String saveSession(User user) {
        String sessionId = String.valueOf(UUID.randomUUID());

        HttpSession httpSession = new HttpSession(sessionId);
        httpSession.setAttribute("user", user);
        HTTP_SESSION_MANGER.add(httpSession);

        return sessionId;
    }
}
