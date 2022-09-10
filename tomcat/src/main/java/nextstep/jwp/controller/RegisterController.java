package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import servlet.mapping.ResponseEntity;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController(UserService userService) {
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

    @Override
    public boolean isMapped(HttpRequest request) {
        return request.getPath().same("/register");
    }
}
