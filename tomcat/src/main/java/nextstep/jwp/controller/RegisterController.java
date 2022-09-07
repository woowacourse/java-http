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
    protected void doGet(HttpRequest request, ResponseEntity entity) {
        entity.clone(ResponseEntity.ok("/register.html"));
    }

    @Override
    protected void doPost(HttpRequest request, ResponseEntity entity) {
        entity.clone(userService.register(request));
    }

    @Override
    public boolean isMapped(HttpRequest request) {
        return request.getPath().same("/register");
    }
}
