package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity login(String account, String password) {
        userService.login(account, password);
        HttpCookie httpCookie = HttpCookie.create();
        httpCookie.putJSessionId();

        return ResponseEntity.cookie(httpCookie, HttpStatus.FOUND, "/index");
    }

    public ResponseEntity signUp(String account, String password, String email) {
        userService.save(account, password, email);

        return ResponseEntity.of(HttpStatus.FOUND, "/index");
    }

}
