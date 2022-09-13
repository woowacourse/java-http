package nextstep.jwp;

import nextstep.jwp.ui.FileController;
import nextstep.jwp.ui.HomeController;
import nextstep.jwp.ui.LoginController;
import nextstep.jwp.ui.RegisterController;
import org.apache.coyote.http11.Configuration;
import org.apache.coyote.http11.RequestMapping;

public class EdenConfig implements Configuration {

    @Override
    public void addControllers(RequestMapping requestMapping) {
        requestMapping.registerController("/", new HomeController());
        requestMapping.registerController("/login", new LoginController());
        requestMapping.registerController("/register", new RegisterController());
    }

    @Override
    public void setFileController(RequestMapping requestMapping) {
        requestMapping.setFileController(new FileController());
    }
}
