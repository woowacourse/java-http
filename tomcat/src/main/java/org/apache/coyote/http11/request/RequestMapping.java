package org.apache.coyote.http11.request;

import org.apache.coyote.http11.controller.DefaultController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;
import org.apache.coyote.http11.Controller;

import java.util.Map;

import static org.apache.coyote.http11.response.ResponsePage.LOGIN_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.REGISTER_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.ROOT_PAGE;

public class RequestMapping {
    private final Map<String, Controller> controllers = Map.of(
            ROOT_PAGE.gerResource(), new RootController(),
            LOGIN_PAGE.gerResource(), new LoginController(),
            REGISTER_PAGE.gerResource(), new RegisterController());

    public Controller getController(final String url) {
        if(controllers.containsKey(url)){
            return controllers.get(url);
        }
        return new DefaultController();
    }
}
