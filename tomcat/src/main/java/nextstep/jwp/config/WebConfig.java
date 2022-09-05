package nextstep.jwp.config;

import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.http11.RequestMapper;

public class WebConfig {

    public static void addControllers() {
        RequestMapper.addController(new HelloController());
        RequestMapper.addController(new LoginController());
        RequestMapper.addController(new ResourceController());
        RequestMapper.addController(new RegisterController());
    }
}
