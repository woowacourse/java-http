package org.apache.catalina.web.controller;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;
import org.apache.catalina.Controller;
import org.apache.catalina.RequestMapping;

public class ApplicationContextConfig {

    public final static Map<String, Controller> controllerMap = Map.of(
            HomeController.ENDPOINT, new HomeController(),
            LoginController.ENDPOINT, new LoginController(),
            RegisterController.ENDPOINT, new RegisterController()
    );

    public final static RequestMapping REQUEST_MAPPING = new RequestMapper(controllerMap);
}
