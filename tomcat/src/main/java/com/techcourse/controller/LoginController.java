package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.List;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import util.ResourceFileLoader;

public class LoginController extends AbstractController {

    private static final String ROOT_LOCATION = "http://localhost:8080/";
    private final String JAVA_SESSION_ID = "JSESSIONID";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        processLogin(response, request.getRequestBody());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        processLoginPage(request, response);
    }

    private void processLogin(HttpResponse response, Map<String, String> requestBody) {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> loginSuccess(response, user),
                        () -> loginFail(response)
                );
    }

    private void loginSuccess(HttpResponse httpResponse, User user) {
        Session session = new Session(user.getId().toString());
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);

        httpResponse.sendRedirect(ROOT_LOCATION);

        HttpCookie httpCookie = new HttpCookie(JAVA_SESSION_ID, user.getId().toString());
        httpResponse.setCookie(httpCookie);
    }

    private void loginFail(HttpResponse httpResponse) {
        httpResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
        httpResponse.setResponseBody(ResourceFileLoader.loadStaticFileToString("/401.html"));
    }

    private void processLoginPage(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (isAlreadyLogin(httpRequest)) {
            httpResponse.sendRedirect(ROOT_LOCATION);
            return;
        }
        httpResponse.setContentType(ContentType.TEXT_HTML);
        httpResponse.setResponseBody(ResourceFileLoader.loadStaticFileToString("/login.html"));
    }

    private boolean isAlreadyLogin(HttpRequest httpRequest) {
        if (!httpRequest.isExistCookie()) {
            return false;
        }

        List<HttpCookie> cookies = httpRequest.getCookies();
        if (!isContainJSessionId(cookies)) {
            return false;
        }

        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(getJSessionId(cookies));
        if (session == null) {
            return false;
        }

        User user = (User) session.getAttribute("user");
        return user != null;
    }

    private boolean isContainJSessionId(List<HttpCookie> cookies) {
        return cookies.stream()
                .anyMatch((cookie) -> cookie.getName().equals(JAVA_SESSION_ID));
    }

    private String getJSessionId(List<HttpCookie> cookies) {
        return cookies.stream()
                .filter((cookie) -> cookie.getName().equals(JAVA_SESSION_ID))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cookie에 JSessionId가 존재하지 않습니다."));
    }
}
