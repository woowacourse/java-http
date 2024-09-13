package org.apache.coyote;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.controller.HomeController;
import org.apache.coyote.controller.StaticResourceController;
import org.apache.coyote.http11.message.request.HttpRequest;

public class RequestMapping {

    private static final List<AbstractController> controllers = new ArrayList<>();

    static {
        UserService userService = new UserService();
        controllers.add(new LoginController(userService));
        controllers.add(new RegisterController(userService));
        controllers.add(new HomeController());
    }

    private RequestMapping() {
    }

    public static AbstractController getController(HttpRequest request) {
        return controllers.stream()
                .filter(controller -> controller.canControl(request))
                .findFirst()
                .orElse(new StaticResourceController());
    }
}
