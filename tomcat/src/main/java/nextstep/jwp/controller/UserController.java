package nextstep.jwp.controller;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User login(String account, String password) {
        return userService.login(account, password);
    }

    public ResponseEntity signUp(String account, String password, String email) {
        userService.save(account, password, email);

        return ResponseEntity.of(HttpStatus.FOUND, "/index");
    }

}
