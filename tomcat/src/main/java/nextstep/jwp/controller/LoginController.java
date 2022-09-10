package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import servlet.handler.AbstractController;
import servlet.mapping.ResponseEntity;

public class LoginController extends AbstractController {

    private static final String PATH = "/login";

    private final UserService userService;

    public LoginController(UserService userService) {
        super(PATH);
        this.userService = userService;
    }

    @Override
    protected ResponseEntity doGet(HttpRequest request) {
        return userService.goLoginPage(request);
    }

    @Override
    protected ResponseEntity doPost(HttpRequest request) {
        return userService.login(request);
    }
}
