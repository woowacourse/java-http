package nextstep.jwp.ui.controller;

import nextstep.jwp.application.LoginService;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import nextstep.jwp.ui.RequestHandler;
import nextstep.jwp.ui.common.HttpSession;
import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            response.sendRedirect("/index.html");
            return;
        }
        response.forward(request.getPath() + ".html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        try {
            User user = loginService.login(account, password);
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.addCookie("JSESSIONID", session.getId());
            response.addHeader("Set-Cookie", response.convertCookieToString());
            response.sendRedirect("/index.html");
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            throw new UnauthorizedException();
        }
    }
}
