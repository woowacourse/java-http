package com.techcourse.controller;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.MimeType;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.StaticResourceProvider;
import org.apache.coyote.http11.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String JSESSIONID = "JSESSIONID";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String INDEX_HTML_PATH = "/index.html";
    private static final String LOGIN_HTML_PATH = "/login.html";
    private static final String ERROR_401_HTML_PATH = "/401.html";

    private final InMemoryUserRepository inMemoryUserRepository = InMemoryUserRepository.getInstance();
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getCookie(JSESSIONID) != null) {
            response.found(INDEX_HTML_PATH);
            return;
        }

        response.setBody(StaticResourceProvider.getStaticResource(LOGIN_HTML_PATH))
                .setContentType(MimeType.HTML.getMimeType());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        if (!request.hasParameter(ACCOUNT) || !request.hasParameter(PASSWORD)) {
            response.found(ERROR_401_HTML_PATH);
            return;
        }

        String account = request.getParameter(ACCOUNT);
        String password = request.getParameter(PASSWORD);
        inMemoryUserRepository.findByAccount(account).ifPresentOrElse(
                user -> login(user, request, response, password),
                () -> response.found(ERROR_401_HTML_PATH)
        );
    }

    private void login(User user, HttpRequest request, HttpResponse response, String password) {
        if (!user.checkPassword(password)) {
            response.found(ERROR_401_HTML_PATH);
            return;
        }

        log.info("user : {}", user);

        if (request.getCookie(JSESSIONID) == null) {
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);
            sessionManager.add(session);
            response.setCookie(JSESSIONID, session.getId())
                    .setHttpOnly(true);
        }
        response.found(INDEX_HTML_PATH);
    }
}
