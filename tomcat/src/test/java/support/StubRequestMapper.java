package support;

import java.util.Map;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.Mapper;
import org.apache.coyote.http11.request.HttpRequest;

public class StubRequestMapper implements Mapper {
    private static final Map<String, AbstractController> controllers = Map.of(
            "/", new HomeController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );
    private static final AbstractController defaultController = new ResourceController();

    @Override
    public Controller getController(final HttpRequest request) {
        String path = request.getPath();
        return controllers.getOrDefault(path, defaultController);
    }
}
