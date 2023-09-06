package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity login(String account, String password) {
        try {
            userService.login(account, password);
            return new ResponseEntity(HttpStatus.FOUND, "/index");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(HttpStatus.FOUND, "/401");
        }
    }

    public ResponseEntity signUp(String account, String password, String email) {
        userService.save(account, password, email);

        return new ResponseEntity(HttpStatus.FOUND, "/index");
    }

}
