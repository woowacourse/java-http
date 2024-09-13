package org.apache.coyote.http11.handler;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.WelcomePageController;
import com.techcourse.service.UserService;
import java.util.Map;

public class HttpHandlerMapperFactory {

    private HttpHandlerMapperFactory() {
    }

    public static HttpControllerMapper create() {
        UserService userService = new UserService();

        return new HttpControllerMapper(Map.of(
                "/", new WelcomePageController(),
                "/login", new LoginController(userService),
                "/register", new RegisterController(userService)
        ));
    }
}
