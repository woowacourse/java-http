package com.techcourse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.file.HtmlReader;
import org.qupring.mvc.controller.AbstractController;

public class LoginController extends AbstractController {

    private static final int FOUND = 302;
    private static final String HTML_CONTENT_TYPE =
            "text/html;charset=utf-8";

    private static final String LOGIN_SUCCESS_PATH = "/index.html";
    private static final String LOGIN_FAILURE_PATH = "/401.html";

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private final LoginSessionService loginSessionService = new LoginSessionService();

    @Override
    protected void doGet(
            HttpRequest request,
            HttpResponse response
    ) {
        if (loginSessionService.isLoggedIn(request)) {
            redirect(response, LOGIN_SUCCESS_PATH);
            return;
        }

        response.setBody(
                HtmlReader.read("static/login.html")
        );
        response.setHeader(
                "Content-Type",
                HTML_CONTENT_TYPE
        );
    }

    @Override
    protected void doPost(
            HttpRequest request,
            HttpResponse response
    ) {

        String account = request.getBody("account");
        String password = request.getBody("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser ->
                        foundUser.checkPassword(password))
                .orElse(null);

        if (user == null) {
            redirect(response, LOGIN_FAILURE_PATH);
            return;
        }

        String sessionId = loginSessionService.login(request, user);
        response.setHeader(
                "Set-Cookie",
                SESSION_COOKIE_NAME + "=" + sessionId + "; Path=/"
        );
        redirect(response, LOGIN_SUCCESS_PATH);
    }

    private void redirect(
            HttpResponse response,
            String location
    ) {
        response.setStatus(FOUND);
        response.setLocation(location);
    }
}
