package org.apache.catalina.web.controller;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.service.SessionService;
import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.catalina.Controller;
import org.apache.catalina.RequestMapping;

public class ApplicationContextConfig {

    public final static UserService USER_SERVICE = new UserService();

    public final static LoginController LOGIN_CONTROLLER = new LoginController(
            new UserService(),
            new SessionService()
    );
    public final static RegisterController REGISTER_CONTROLLER = new RegisterController(USER_SERVICE);

    public final static Map<String, Controller> controllerMap = Map.of(
            HomeController.ENDPOINT, new HomeController(),
            LoginController.ENDPOINT, LOGIN_CONTROLLER,
            RegisterController.ENDPOINT, REGISTER_CONTROLLER
    );

    public final static RequestMapping REQUEST_MAPPING = new RequestMapper(controllerMap);
}
