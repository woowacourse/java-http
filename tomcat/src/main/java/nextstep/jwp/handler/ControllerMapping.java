package nextstep.jwp.handler;

import java.util.List;
import org.apache.catalina.exception.NotSupportedRequestException;
import org.apache.catalina.servlet.Controller;
import org.apache.coyote.http.vo.HttpRequest;

public class ControllerMapping {

    private static final List<Controller> controllers = List.of(
            new DefaultPageController(),
            new LoginController(),
            new RegisterController()
    );

    private ControllerMapping() {
    }

    public static boolean hasSupportedController(final HttpRequest request) {
        return controllers.stream()
                .anyMatch(it -> it.isSupported(request));
    }

    public static Controller getSupportedController(final HttpRequest request) {
        return controllers.stream()
                .filter(it -> it.isSupported(request))
                .findFirst()
                .orElseThrow(NotSupportedRequestException::new);
    }
}
