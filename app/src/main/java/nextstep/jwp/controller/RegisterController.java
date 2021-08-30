package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        log.debug("HTTP GET Register Request: {}", request.getPath());
        response.responseOk("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        log.debug("HTTP POST Register Request: {}", request.getPath());
        try {
            userService.signUp(request);
            response.responseRedirect("http://" + request.getHeader("Host") + "/index.html");
        } catch (IllegalArgumentException exception) {
            response.responseRedirect("http://" + request.getHeader("Host") + "/register.html");
        }
    }
}
