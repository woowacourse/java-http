package nextstep.jwp;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.service.UserService;

public class ApplicationContext {

    public static UserService userService() {
        return new UserService();
    }

    public static LoginController loginController() {
        return new LoginController(userService());
    }

    public static RegisterController registerController() {
        return new RegisterController(userService());
    }
}
