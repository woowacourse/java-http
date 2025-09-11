package com.techcourse;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.service.LoginService;
import com.techcourse.service.RegisterService;
import java.util.List;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.ControllerProvider;

public class Application {

    public static void main(String[] args) {
        registerControllers();

        final var tomcat = new Tomcat();
        tomcat.start();
    }

    private static void registerControllers() {
        final List<Controller> controllers = List.of(
                new RegisterController(new RegisterService()),
                new LoginController(new LoginService()),
                new StaticResourceController()
        );

        ControllerProvider.INSTANCE.register(controllers);
    }
}
