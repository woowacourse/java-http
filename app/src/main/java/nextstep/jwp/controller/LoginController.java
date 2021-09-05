package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpResponseStatus;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;

import java.util.Optional;

public class LoginController extends AbstractController{

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpRequest.getSession().ifPresentOrElse(httpSession -> handleLoginWhenHaveSession(httpSession, httpResponse), () -> {
            httpResponse.status(HttpResponseStatus.OK);
            httpResponse.resource("/login.html");
        });
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        User loginUser = userService.login(httpRequest.getQueryValue("account"), httpRequest.getQueryValue("password"));
        createSession(httpRequest, httpResponse, loginUser);
        httpResponse.status(HttpResponseStatus.FOUND);
        httpResponse.location("/index.html");
    }

    private void handleLoginWhenHaveSession(HttpSession httpSession, HttpResponse httpResponse) {
        if (isLogin(httpSession)) {
            httpResponse.status(HttpResponseStatus.FOUND);
            httpResponse.location("/index.html");
        } else {
            httpResponse.status(HttpResponseStatus.OK);
            httpResponse.resource("/login.html");
        }
    }

    private void createSession(HttpRequest httpRequest, HttpResponse httpResponse, User loginUser) {
        setJSessionId(httpRequest, httpResponse);
        httpRequest.getSession().ifPresent(httpSession -> httpSession.setAttribute("user", loginUser));
    }

    private Optional<User> getUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return Optional.ofNullable(user);
    }

    private boolean isLogin(HttpSession httpSession) {
        Optional<User> user = getUser(httpSession);
        return user.isPresent();
    }
}
