package nextstep.joanne.dashboard.controller;

import nextstep.joanne.dashboard.model.User;
import nextstep.joanne.dashboard.service.UserService;
import nextstep.joanne.server.handler.controller.AbstractController;
import nextstep.joanne.server.http.HttpSession;
import nextstep.joanne.server.http.HttpStatus;
import nextstep.joanne.server.http.request.ContentType;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        log.debug("HTTP POST Login Request from {}", request.uri());

        User user = userService.login(request.bodyOf("account"), request.bodyOf("password"));

        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        response.addStatus(HttpStatus.FOUND);
        response.addHeaders("Location", "/index.html");
        response.addHeaders("Content-Type", ContentType.resolve(request.uri()));
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        log.debug("HTTP GET Login Request from {}", request.uri());

        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("user"))) {
            response.redirect("/index.html");
            return;
        }

        response.addStatus(HttpStatus.OK);
        response.addHeaders("Content-Type", ContentType.resolve(request.uri()));
        response.addBody(request.uri());
    }
}
