package nextstep.jwp.controller;

import org.springframework.web.servlet.mvc.Controller;

public class UserController implements Controller {

    private static final UserController USER_CONTROLLER = new UserController();

    private UserController() {
    }

    public static UserController getInstance() {
        return USER_CONTROLLER;
    }
}
