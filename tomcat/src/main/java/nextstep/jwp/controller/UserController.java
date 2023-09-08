package nextstep.jwp.controller;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public class UserController {

    public static final UserController INSTANCE = new UserController(UserService.getInstance());

    private final UserService userService;

    private UserController(UserService userService) {
        this.userService = userService;
    }

    public static UserController getInstance() {
        return INSTANCE;
    }

    public User login(String account, String password) {
        return userService.login(account, password);
    }

    public ResponseEntity signUp(String account, String password, String email) {
        userService.save(account, password, email);

        return ResponseEntity.of(HttpStatus.FOUND, "/index");
    }

}
