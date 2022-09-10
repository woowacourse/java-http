package nextstep.jwp.config;

import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootController;
import org.apache.catalina.core.Configuration;
import org.apache.catalina.core.RequestMapping;

public class AppConfiguration implements Configuration {

    @Override
    public void addController(final RequestMapping requestMapping) {
        requestMapping.addController("/", new RootController());
        requestMapping.addController("/index.html", new IndexController());
        requestMapping.addController("/login", new LoginController());
        requestMapping.addController("/register", new RegisterController());
    }
}
