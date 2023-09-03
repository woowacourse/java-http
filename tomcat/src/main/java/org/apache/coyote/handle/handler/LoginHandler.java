package org.apache.coyote.handle.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.Session;
import org.apache.coyote.common.SessionManager;
import org.apache.coyote.request.HttpCookie;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final String LOGIN_PAGE = "/login.html";
    private static final String LOGIN_SUCCESS_PAGE = "/index.html";
    private static final String LOGIN_FAIL_PAGE = "/401.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final HttpCookie httpCookie = httpRequest.getCookie();
        final String cookie = httpCookie.getCookie(JSESSIONID);
        if (cookie == null || SessionManager.findSession(cookie) == null) {
            renderPage(httpResponse, HttpStatus.OK, LOGIN_PAGE);
            return;
        }
        renderPage(httpResponse, HttpStatus.FOUND, LOGIN_SUCCESS_PAGE);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final Map<String, String> body = httpRequest.getBody(ContentType.APPLICATION_JSON);
        final String account = body.get(ACCOUNT);
        final String password = body.get(PASSWORD);
        if (account == null || password == null) {
            log.warn("Account Or Password Not Exist");
            renderPage(httpResponse, HttpStatus.FOUND, LOGIN_FAIL_PAGE);
            return;
        }

        final Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
        if (findUser.isEmpty() || !findUser.get().checkPassword(password)) {
            log.warn("Login Fail");
            renderPage(httpResponse, HttpStatus.FOUND, LOGIN_FAIL_PAGE);
            return;
        }
        loginSuccess(httpResponse, findUser.get());
    }

    private void loginSuccess(final HttpResponse httpResponse, final User user) throws IOException {
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        SessionManager.add(session);
        httpResponse.addCookie(JSESSIONID, session.getId());
        renderPage(httpResponse, HttpStatus.FOUND, LOGIN_SUCCESS_PAGE);
    }

    private void renderPage(
            final HttpResponse httpResponse,
            final HttpStatus httpStatus,
            final String page
    ) throws IOException {
        final String body = getBody(page);
        httpResponse.setStatus(httpStatus);
        if (httpStatus == HttpStatus.FOUND) {
            httpResponse.setLocation(page);
        } else {
            httpResponse.setContentType(ContentType.TEXT_HTML.getType());
            httpResponse.setContent(body);
        }
    }

    private String getBody(final String page) throws IOException {
        final URL resource = ClassLoader.getSystemClassLoader().getResource(STATIC_DIRECTORY + page);
        final File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
