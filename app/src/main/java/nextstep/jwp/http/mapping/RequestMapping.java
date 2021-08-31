package nextstep.jwp.http.mapping;

import nextstep.jwp.app.ui.Controller;
import nextstep.jwp.app.ui.DefaultController;
import nextstep.jwp.app.ui.LoginController;
import nextstep.jwp.app.ui.RegisterController;
import nextstep.jwp.app.ui.ResourceController;
import nextstep.jwp.http.request.HttpRequest;

public class RequestMapping {

    public static Controller getController(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        if ("/".equals(path)) {
            return new DefaultController();
        }
        if ("/login".equals(path)) {
            return new LoginController();
        }
        if ("/register".equals(path)) {
            return new RegisterController();
        }
        return new ResourceController();
    }
}
