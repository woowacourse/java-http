package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import servlet.handler.AbstractController;
import servlet.mapping.ResponseEntity;

public class RegisterController extends AbstractController {

    private static final String PATH = "/register";

    private final UserService userService;

    public RegisterController(UserService userService) {
        super(PATH);
        this.userService = userService;
    }

    @Override
    protected ResponseEntity doGet(HttpRequest request) {
        return ResponseEntity.ok("/register.html");
    }

    @Override
    protected ResponseEntity doPost(HttpRequest request) {
        return userService.register(request);
    }
}
