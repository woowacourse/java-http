package org.apache.coyote.http11;

import com.techcourse.controller.DefaultController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import java.util.Objects;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    public static Controller getController(HttpRequest request) {
        if (request.getUrl().equals("/")) {
            return new RootController();
        }
        if (request.getUrl().equals("/login")) {
            return new LoginController();
        }
        if (request.getUrl().equals("/register")) {
            return new RegisterController();
        }
        return new DefaultController();
    }
}
