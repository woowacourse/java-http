package nextstep.jwp.config;

import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.http11.RequestMapping;

public class WebConfig {

    public static void addControllers() {
        RequestMapping.addController(new HelloController());
        RequestMapping.addController(new LoginController());
        RequestMapping.addController(new ResourceController());
        RequestMapping.addController(new RegisterController());
    }
}
