package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import util.ResourceFileLoader;

public class LoginController extends AbstractController {

    private final String JAVA_SESSION_ID = "JSESSIONID";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        processLogin(response, request.getRequestBody());
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        processLoginPage(response);
    }

    private void processLogin(HttpResponse response, Map<String, String> requestBody) throws IOException {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                loginSuccess(response, user);
                return;
            }
        }
        loginFail(response);
    }

    private void processLoginPage(HttpResponse httpResponse) throws IOException {
        httpResponse.setContentType(ContentType.TEXT_HTML);
        httpResponse.setResponseBody(ResourceFileLoader.loadStaticFileToString("/login.html"));
    }

    private void loginSuccess(HttpResponse httpResponse, User user) {
        Session session = new Session(user.getId().toString());
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);

        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.setLocation("http://localhost:8080/");

        HttpCookie httpCookie = new HttpCookie(JAVA_SESSION_ID, user.getId().toString());
        httpResponse.setCookie(httpCookie);
    }

    private void loginFail(HttpResponse httpResponse) throws IOException {
        httpResponse.setHttpStatus(HttpStatus.UNAUTHORIZED);
        httpResponse.setResponseBody(ResourceFileLoader.loadStaticFileToString("/401.html"));
    }
}
