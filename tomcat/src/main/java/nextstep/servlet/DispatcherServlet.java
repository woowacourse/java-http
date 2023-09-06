package nextstep.servlet;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ResponseEntity;
import nextstep.jwp.controller.StaticResourceController;
import nextstep.jwp.controller.rest.LoginController;
import nextstep.jwp.controller.rest.RegisterController;
import nextstep.servlet.filter.Interceptor;
import nextstep.servlet.filter.SessionInterceptor;
import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class DispatcherServlet implements Servlet {

    private final List<Interceptor> interceptors = List.of(
            new SessionInterceptor()
    );

    private final List<Controller> controllers = List.of(
            new LoginController(),
            new RegisterController(),
            new StaticResourceController()
    );

    public DispatcherServlet() {
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (passInterceptors(request, response)) {
            final var controller = findController(request);
            final ResponseEntity responseEntity = controller.handle(request);

            response.setStatus(responseEntity.getStatusCode());
            responseEntity.getHeaders().forEach(response::setHeader);
            response.setBody(responseEntity.getBody());
        }
    }

    private boolean passInterceptors(HttpRequest request, HttpResponse response) {
        return interceptors.stream()
                           .filter(interceptor -> interceptor.supports(request))
                           .allMatch(interceptor -> interceptor.preHandle(request, response));
    }

    private Controller findController(HttpRequest request) {
        return controllers.stream()
                          .filter(candidate -> candidate.canHandle(request))
                          .findAny()
                          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경로입니다."));
    }
}
