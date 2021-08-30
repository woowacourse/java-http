package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        log.debug("HTTP GET Login Request: {}", request.getPath());
        response.responseOk("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        log.debug("HTTP POST Login Request: {}", request.getPath());
        try {
            userService.login(request);
            response.responseRedirect("http://" + request.getHeader("Host") + "/index.html");
        } catch (IllegalArgumentException exception) {
            response.responseRedirect("http://" + request.getHeader("Host") + "/401.html");
        }
    }
}
