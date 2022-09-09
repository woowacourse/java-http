package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.session.Cookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String USER = "user";

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, Object> parameters = httpRequest.getRequestBody().getParameters();
        final String account = (String) parameters.get("account");
        final String password = (String) parameters.get("password");

        Optional<User> user = findUser(account);

        if (user.isEmpty()) {
            httpResponse.unAuthorized();
            return;
        }

        User existedUser = user.orElseThrow();

        if (!existedUser.checkPassword(password)) {
            httpResponse.unAuthorized();
            return;
        }

        log.info("로그인 성공! 아이디: " + existedUser.getAccount());
        setSession(httpRequest, existedUser);

        httpResponse.found("/index.html")
                .setCookie(new Cookie(Map.of("JSESSIONID", httpRequest.getSession().getId())));
    }

    private Optional<User> findUser(String account) {
        return InMemoryUserRepository.findByAccount(account);
    }

    private void setSession(HttpRequest httpRequest, User user) {
        SessionManager sessionManager = new SessionManager();
        Session session = httpRequest.getSession();
        session.setAttribute(USER, user);
        sessionManager.add(session);
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Session session = httpRequest.getSession();
        if (session.getAttribute(USER) != null) {
            httpResponse.found("/index.html");
            return;
        }

        String responseBody = getBody();
        httpResponse.ok(ContentType.HTML, responseBody);
    }

    private String getBody() throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}


