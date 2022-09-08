package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import servlet.mapping.ResponseEntity;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, ResponseEntity entity) {
        entity.clone(userService.goLoginPage(request));
    }

    @Override
    protected void doPost(HttpRequest request, ResponseEntity entity) {
        entity.clone(userService.login(request));
    }

    @Override
    public boolean isMapped(HttpRequest request) {
        return request.getPath().same("/login");
    }
}
