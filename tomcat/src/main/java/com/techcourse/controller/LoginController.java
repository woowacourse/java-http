package com.techcourse.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ACCOUNT_PARAM = "account";
    private static final String PASSWORD_PARAM = "password";
    private static final String INDEX_PATH = "/index.html";
    private static final String ERROR_401_PATH = "/401.html";
    private static final String RESOURCE_BASE_PATH = "static";
    public static final String TEXT_HTML = "text/html";
    private static final String JSESSIONID_COOKIE = "JSESSIONID=";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String resource = ensureHtmlExtension(request.getPath());
        String responseBody = loadResourceContent(resource);
        boolean containsCookie = request.containsHeaders(HttpHeader.COOKIE.getValue());
        if (containsCookie) {
            HttpCookie httpCookie = new HttpCookie(request.getHeader(HttpHeader.COOKIE.getValue()));
            handleCookieRequest(httpCookie, responseBody, response);
        }

        if (!containsCookie) {
            buildOkResponse(responseBody, response);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter(ACCOUNT_PARAM);
        String password = request.getParameter(PASSWORD_PARAM);
        if (findUserByInfo(account, password)) {
            handleSuccessfulLogin(response, account);
            return;
        }

        handleFailedLogin(response);
    }

    private void handleCookieRequest(HttpCookie httpCookie, String responseBody, HttpResponse response) {
        if (httpCookie.containsJSessionId()) {
            String sessionId = httpCookie.getJSessionId();
            Session session = SessionManager.getInstance().findSession(sessionId);
            if (session == null) {
                buildOkResponse(responseBody, response);
                return;
            }
            buildRedirectResponse(INDEX_PATH, response);
            return;
        }

        buildOkResponse(responseBody, response);
    }

    private void handleSuccessfulLogin(HttpResponse response, String account) {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        SessionManager.getInstance().add(session);

        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {
                    session.setAttribute(sessionId, user);
                    buildRedirectWithCookieResponse(INDEX_PATH, sessionId, response);
                });
    }

    private void handleFailedLogin(HttpResponse response) {
        buildRedirectResponse(ERROR_401_PATH, response);
    }

    private boolean findUserByInfo(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> {
                    log.info(user.toString());
                    return true;
                })
                .orElse(false);
    }

    private String loadResourceContent(String resource) throws IOException {
        String resourcePath = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource(RESOURCE_BASE_PATH + resource))
                .getPath();

        try (FileInputStream file = new FileInputStream(resourcePath)) {
            return new String(file.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void buildOkResponse(String responseBody, HttpResponse response) {
        response.setStatusCode(StatusCode.OK);
        response.setResponseBody(responseBody);
        response.addHeader(HttpHeader.CONTENT_TYPE.getValue(), TEXT_HTML);
        response.addHeader(HttpHeader.CONTENT_LENGTH.getValue(), String.valueOf(responseBody.getBytes().length));
    }

    private void buildRedirectResponse(String location, HttpResponse response) {
        response.setStatusCode(StatusCode.FOUND);
        response.addHeader(HttpHeader.LOCATION.getValue(), location);
    }

    private void buildRedirectWithCookieResponse(String location, String sessionId, HttpResponse response) {
        response.setStatusCode(StatusCode.FOUND);
        response.addHeader(HttpHeader.LOCATION.getValue(), location);
        response.addHeader(HttpHeader.SET_COOKIE.getValue(), JSESSIONID_COOKIE + sessionId);
    }

    private String ensureHtmlExtension(String path) {
        if (!path.contains(".")) {
            path += ".html";
        }

        return path;
    }
}
