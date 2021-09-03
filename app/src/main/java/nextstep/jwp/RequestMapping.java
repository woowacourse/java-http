package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.FrontController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.Request;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = new HashMap<>();
    private static final String LOGIN = "/login";
    private static final String REGISTER = "/register";
    private static final String FRONT = "front";

    static {
        LoginService loginService = new LoginService();
        CONTROLLERS.put(LOGIN, new LoginController(loginService));
        CONTROLLERS.put(REGISTER, new RegisterController(new RegisterService(), loginService));
        CONTROLLERS.put(FRONT, new FrontController());
    }

    public Controller getController(Request request) {
        if (request.isUriMatch(LOGIN)) {
            return CONTROLLERS.get(LOGIN);
        }
        if (request.isUriMatch(REGISTER)) {
            return CONTROLLERS.get(REGISTER);
        }
        if (request.isUriFile()) {
            return CONTROLLERS.get(FRONT);
        }
        throw new NotFoundException();
    }
}
