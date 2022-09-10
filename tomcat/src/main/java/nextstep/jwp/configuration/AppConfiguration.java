package nextstep.jwp.configuration;

import nextstep.jwp.controller.ExceptionController;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootController;
import org.apache.catalina.core.Configuration;
import org.apache.catalina.core.RequestMapping;

public class AppConfiguration implements Configuration {

    @Override
    public void addController(final RequestMapping requestMapping) {
        requestMapping.setController("/", new RootController());
        requestMapping.setController("/index.html", new IndexController());
        requestMapping.setController("/login", new LoginController());
        requestMapping.setController("/register", new RegisterController());
        requestMapping.setController("/401.html", new ExceptionController());
    }
}
