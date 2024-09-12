package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.DefaultController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import java.util.Objects;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    public static Controller getController(HttpRequest request) {
        if (Objects.equals(request.getUrl(), "/")) {
            return new RootController();
        }
        if (Objects.equals(request.getUrl(), "/login")) {
            return new LoginController();
        }
        if (Objects.equals(request.getUrl(), "/register")) {
            return new RegisterController();
        }
        return new DefaultController();
    }
}
