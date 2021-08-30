package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.web.ContentType;
import nextstep.jwp.web.HttpRequest;
import nextstep.jwp.web.HttpResponse;
import nextstep.jwp.web.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoginController extends AbstractController {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoginProcess(request)) {
            loginProcess(request, response);
            return;
        }
        loginPageProcess(response);
    }

    private boolean isLoginProcess(HttpRequest request) {
        return request.getParameter("account") != null && request.getParameter("password") != null;
    }

    public void loginProcess(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        foundUser -> passwordCheckProcess(foundUser, password, response),
                        () -> responseUnauthorized(response));
    }

    private void passwordCheckProcess(User foundUser, String password, HttpResponse response) {
        if (foundUser.checkPassword(password)) {
            response.status(HttpStatus.OK)
                    .contentType(ContentType.PLAIN.toHttpNotation())
                    .body("login success!\r\n" +
                            "account: " + foundUser.getAccount() + "\r\n" +
                            "email: " + foundUser.getEmail());
        }
        responseUnauthorized(response);
    }

    private void responseUnauthorized(HttpResponse response) {
        response.status(HttpStatus.FOUND)
                .location("/401.html");
    }

    private void loginPageProcess(HttpResponse response) throws IOException {
        String loginHtmlPath = getClass().getClassLoader().getResource("static/login.html").getPath();

        response.status(HttpStatus.OK)
                .contentType("text/html;charset=utf-8")
                .body(new String(Files.readAllBytes(Path.of(loginHtmlPath))));
    }
}
