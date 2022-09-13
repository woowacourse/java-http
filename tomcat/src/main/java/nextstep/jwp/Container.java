package nextstep.jwp;

import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.service.AuthService;

public class Container {

    public static final AuthService authService;
    public static final IndexController indexController;
    public static final LoginController loginController;
    public static final RegisterController registerController;

    static {
        authService = new AuthService();
        indexController = new IndexController();
        loginController = new LoginController(authService);
        registerController = new RegisterController(authService);
    }

    private Container() {
    }
}
