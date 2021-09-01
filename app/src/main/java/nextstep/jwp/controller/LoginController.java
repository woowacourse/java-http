package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.web.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class LoginController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request)) {
            redirectHomePage(response);
            return;
        }
        if (isLoginProcess(request)) {
            loginProcess(request, response);
            return;
        }
        loginPageProcess(response);
    }

    private boolean isLoggedIn(HttpRequest request) {
        return Objects.nonNull(request.getCookie(HttpSession.SESSION_NAME));
    }

    private boolean isLoginProcess(HttpRequest request) {
        return request.getParameter("account") != null && request.getParameter("password") != null;
    }

    public void loginProcess(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        foundUser -> passwordCheckProcess(foundUser, password, request, response),
                        () -> responseUnauthorized(response));
    }

    private void passwordCheckProcess(User foundUser, String password, HttpRequest request, HttpResponse response) {
        if (foundUser.checkPassword(password)) {
            issueSession(foundUser, request, response);
            redirectHomePage(response);
            return;
        }
        responseUnauthorized(response);
    }

    private void issueSession(User foundUser, HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession();
        session.setAttribute("user", foundUser);

        Cookie sessionCookie = new Cookie(HttpSession.SESSION_NAME, session.getId());
        response.setCookie(sessionCookie);
    }

    private void redirectHomePage(HttpResponse response) {
        response.status(HttpStatus.FOUND)
                .location("/index.html");
    }

    private void responseUnauthorized(HttpResponse response) {
        response.status(HttpStatus.FOUND)
                .location("/401.html");
    }

    private void loginPageProcess(HttpResponse response) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String loginHtmlPath = resource.getPath();

        response.status(HttpStatus.OK)
                .contentType(ContentType.toHttpNotationFromFileExtension(resource.getFile()))
                .body(new String(Files.readAllBytes(Path.of(loginHtmlPath))));
    }
}
