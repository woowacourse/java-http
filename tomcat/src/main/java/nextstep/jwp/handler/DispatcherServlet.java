package nextstep.jwp.handler;

import org.apache.catalina.servlet.AbstractController;
import org.apache.catalina.servlet.Controller;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;

public class DispatcherServlet extends AbstractController {

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Controller controller = getSupportedController(httpRequest);
        controller.service(httpRequest, httpResponse);
    }

    @Override
    public boolean isSupported(final HttpRequest httpRequest) {
        return ControllerMapping.hasSupportedController(httpRequest);
    }

    private Controller getSupportedController(final HttpRequest httpRequest) {
        return ControllerMapping.getSupportedController(httpRequest);
    }
}
