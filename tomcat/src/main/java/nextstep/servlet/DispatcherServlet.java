package nextstep.servlet;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestURI;
import org.apache.coyote.http11.HttpResponse;

public class DispatcherServlet {

    private final List<Controller> controllers = List.of(
        new LoginController(),
        new ResourceController()
    );

    public HttpResponse service(final HttpRequest httpRequest) throws IOException {
        final Controller controller = findController(httpRequest);
        return controller.service(httpRequest);
    }

    private Controller findController(final HttpRequest httpRequest) {
        final HttpRequestURI requestURI = httpRequest.getRequestURI();
        return controllers.stream()
            .filter(controller -> controller.canHandle(requestURI))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("요청을 처리할 수 없습니다."));
    }
}
