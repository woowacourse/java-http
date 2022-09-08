package nextstep.jwp.controller;

import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http11.message.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> values;

    static {
        values = Map.of(
                "/", new HelloWorldController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
        );
    }

    private RequestMapping() {
    }

    public static Controller getController(final HttpRequest request) {
        String path = request.getRequestUri().getPath();
        Controller controller = values.get(path);

        if (Objects.isNull(controller)) {
            return new StaticFileController();
        }
        return controller;
    }
}
