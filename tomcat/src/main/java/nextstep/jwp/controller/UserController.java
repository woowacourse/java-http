package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.ResponseEntity;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity login(RequestBody requestBody) {
        String account = requestBody.get("account");
        String password = requestBody.get("password");

        try {
            userService.login(account, password);
            return new ResponseEntity(HttpMethod.POST, HttpStatus.FOUND, "/index");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(HttpMethod.POST, HttpStatus.FOUND, "/401");
        }
    }

    public ResponseEntity signUp(RequestBody requestBody) {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");

        userService.save(account, password, email);

        return new ResponseEntity(HttpMethod.POST, HttpStatus.FOUND, "/index");
    }
}
