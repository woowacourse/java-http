package nextstep.jwp.web.ui;

import nextstep.jwp.server.RequestHandler;
import nextstep.jwp.server.exception.UnauthorizedException;
import nextstep.jwp.server.http.common.HttpSession;
import nextstep.jwp.server.http.request.HttpRequest;
import nextstep.jwp.server.http.response.HttpResponse;
import nextstep.jwp.web.application.LoginService;
import nextstep.jwp.web.exception.UserNotFoundException;
import nextstep.jwp.web.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

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
