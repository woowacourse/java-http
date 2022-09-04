package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.apache.coyote.http11.response.element.HttpStatus;
import servlet.mapping.ResponseEntity;

public class Controller {

    private final UserService userService;

    public Controller(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity welcome(HttpMethod method) {
        return new ResponseEntity(method, "/welcome.html", HttpStatus.OK);
    }

    public ResponseEntity login(HttpMethod method, String path) {
        userService.login(path);
        return new ResponseEntity(method, "/login.html", HttpStatus.OK);
    }

    public ResponseEntity findResource(HttpMethod method, String path) {
        return new ResponseEntity(method, path, HttpStatus.OK);
    }
}
