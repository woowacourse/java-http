package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.element.Path;
import servlet.mapping.ResponseEntity;

public class ControllerImpl {

    private final UserService userService;

    public ControllerImpl(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity welcome() {
        return ResponseEntity.ok("/welcome.html");
    }

    public ResponseEntity goLoginPage() {
        return ResponseEntity.ok("/login.html");
    }

    public ResponseEntity login(HttpRequest request) {
        return userService.login(request);
    }

    public ResponseEntity findResource(Path path) {
        return ResponseEntity.ok(path.getPath());
    }

    public ResponseEntity goRegisterPage() {
        return ResponseEntity.ok("/register.html");
    }

    public ResponseEntity register(HttpRequest request) {
        return userService.register(request);
    }
}
