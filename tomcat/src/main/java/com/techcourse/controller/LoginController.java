package com.techcourse.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final String RESOURCE_BASE_PATH = "static";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse.HttpResponseBuilder response) throws Exception {
        String resource = ensureHtmlExtension(request.getPath());
        String responseBody = loadResourceContent(resource);
        if (request.containsCookie()) {
            HttpCookie httpCookie = new HttpCookie(request.getCookie());
            handleCookieRequest(httpCookie, responseBody, response);
        }

        if (!request.containsCookie()) {
            buildOkResponse(responseBody, response);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse.HttpResponseBuilder response) {
        String requestBody = request.getRequestBody();
        String account = getParameter(requestBody, "account");
        String password = getParameter(requestBody, "password");

        if (findUserByInfo(account, password)) {
            handleSuccessfulLogin(response, account);
        }

        if (!findUserByInfo(account, password)) {
            handleFailedLogin(response);
        }
    }

    private void handleCookieRequest(HttpCookie httpCookie, String responseBody, HttpResponse.HttpResponseBuilder response) {
        if (httpCookie.containsJSessionId()) {
            String sessionId = httpCookie.getJSessionId();
            Session session = SessionManager.getInstance().findSession(sessionId);
            if (session == null) {
                buildOkResponse(responseBody, response);
                return;
            }
            buildRedirectResponse("/index.html", response);
        }

        if (!httpCookie.containsJSessionId()) {
            buildOkResponse(responseBody, response);
        }
    }

    private void handleSuccessfulLogin(HttpResponse.HttpResponseBuilder response, String account) {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        SessionManager.getInstance().add(session);

        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {
                    session.setAttribute(sessionId, user);
                    buildRedirectWithCookieResponse("/index.html", sessionId, response);
                });
    }

    private void handleFailedLogin(HttpResponse.HttpResponseBuilder response) {
        buildRedirectResponse("/401.html", response);
    }

    private String getParameter(String requestBody, String key) {
        return Arrays.stream(requestBody.split("&"))
                .filter(param -> param.startsWith(key + "="))
                .map(param -> param.split("=")[1])
                .findFirst()
                .orElse("");
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

    private void buildOkResponse(String responseBody, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode("200 OK")
                .withResponseBody(responseBody)
                .addHeader("Content-Type", "text/html")
                .addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

    private void buildRedirectResponse(String location, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode("302 Found")
                .addHeader("Location", location);
    }

    private void buildRedirectWithCookieResponse(String location, String sessionId, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode("302 Found")
                .addHeader("Location", location)
                .addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
    }

    private String ensureHtmlExtension(String path) {
        if (!path.contains(".")) {
            path += ".html";
        }

        return path;
    }
}
