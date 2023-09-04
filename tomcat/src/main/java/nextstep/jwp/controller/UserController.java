package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ResponseEntity;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity login(String account, String password) {
        try {
            userService.login(account, password);
            return ResponseEntity.of(HttpStatus.FOUND, "/index");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.of(HttpStatus.UNAUTHORIZED, "/401");
        }
    }
}
