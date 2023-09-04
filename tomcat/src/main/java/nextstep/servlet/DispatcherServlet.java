package nextstep.servlet;

import java.io.IOException;
import java.util.List;
import nextstep.filter.Interceptor;
import nextstep.filter.LoginInterceptor;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.controller.ResponseEntity;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class DispatcherServlet {

    private final List<Controller> controllers = List.of(
        new LoginController(),
        new RegisterController(),
        new ResourceController()
    );
    private final List<Interceptor> interceptors = List.of(
        new LoginInterceptor()
    );

    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (!prehandleInterceptors(httpRequest, httpResponse)) {
            return;
        }
        final Controller controller = findController(httpRequest);
        final ResponseEntity responseBody = controller.service(httpRequest);
        httpResponse.addAttributes(responseBody);
    }

    private boolean prehandleInterceptors(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        return interceptors.stream()
            .filter(interceptor -> interceptor.support(httpRequest))
            .allMatch(interceptor -> interceptor.preHandle(httpRequest, httpResponse));
    }

    private Controller findController(final HttpRequest httpRequest) {
        return controllers.stream()
            .filter(controller -> controller.canHandle(httpRequest))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("요청을 처리할 수 없습니다."));
    }
}
