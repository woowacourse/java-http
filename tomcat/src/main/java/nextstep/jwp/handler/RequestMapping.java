package nextstep.jwp.handler;

import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.DefaultPageController;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticFileController;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> values =
        Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController(),
            "/index.html", new IndexController(),
            "/", new DefaultPageController()
            );

    private static final Controller staticFileController = new StaticFileController();

    public static Controller getController(HttpRequest request) {
        return values.getOrDefault(request.getUri(), staticFileController);
    }
}
