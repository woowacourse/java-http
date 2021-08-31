package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.UUID;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.handler.Cookie;
import nextstep.jwp.handler.HttpBody;
import nextstep.jwp.handler.HttpSession;
import nextstep.jwp.handler.HttpSessions;
import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.handler.response.HttpResponse;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, URISyntaxException {
        try {
            HttpSession session = httpRequest.getSession();
            if (session.containsAttribute("user")) {
                httpResponse.redirect("/index.html");
                return;
            }
        } catch (NoSuchElementException e) {
            httpResponse.ok(httpRequest.getRequestUrl() + ".html");
            return;
        }

        if (httpRequest.isUriContainsQuery()) {
            doGetWithQuery(httpRequest, httpResponse);
            return;
        }
        httpResponse.ok(httpRequest.getRequestUrl() + ".html");
    }

    private void doGetWithQuery(HttpRequest httpRequest, HttpResponse httpResponse) {}

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, URISyntaxException {
        HttpBody httpBody = httpRequest.getBody();
        String account = httpBody.getBodyParams("account");
        String password = httpBody.getBodyParams("password");

        try {
            User user = findUser(account);
            if (!user.checkPassword(password)) {
                throw new LoginException("User의 정보와 입력한 정보가 일치하지 않습니다.");
            }

            if (httpRequest.containsCookie("JSESSIONID")) {
                final HttpSession session = httpRequest.getSession();
                session.setAttribute("user", user);

                httpResponse.redirect("/index.html");
                return;
            }

            String sessionId = saveSession();
            HttpSession session = HttpSessions.getSession(sessionId);
            session.setAttribute("user", user);

            httpResponse.setCookie(new Cookie("JSESSIONID", sessionId));
            httpResponse.redirect("/index.html");
        } catch (LoginException | NoSuchElementException e) {
            httpResponse.unauthorized("/401.html");
        }
    }

    private String saveSession() {
        String sessionId = UUID.randomUUID().toString();
        HttpSessions.add(sessionId);
        return sessionId;
    }

    private User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                                     .orElseThrow(() -> { throw new LoginException("해당 User가 존재하지 않습니다."); });
    }
}
