package nextstep.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.servlet.interceptor.Interceptor;
import nextstep.servlet.interceptor.SessionInterceptor;
import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestLine;
import org.apache.coyote.http11.message.response.HttpResponse;

public class DispatcherServlet implements Servlet {

    private final Map<List<RequestLine>, Interceptor> interceptors = Map.of(
            List.of(
                    new RequestLine(HttpMethod.GET, "/login"),
                    new RequestLine(HttpMethod.POST, "/login")
            )
            , new SessionInterceptor()
    );

    private final List<Controller> controllers = List.of(
            new HomeController(),
            new LoginController(),
            new RegisterController()
    );

    private final StaticResourceResolver staticResourceResolver = new StaticResourceResolver();

    public DispatcherServlet() {
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (passInterceptors(request, response)) {
            final var controller = findController(request);
            final var responseEntity = controller.handle(request);

            response.setStatus(responseEntity.getStatusCode());
            responseEntity.getHeaders().forEach(response::setHeader);

            if (responseEntity.isRestResponse()) {
                response.setBody(responseEntity.getBody());
                return;
            }
            final var staticResource = staticResourceResolver.findFileContentByPath(responseEntity.getBody());
            response.setBody(staticResource);
        }
    }

    private boolean passInterceptors(HttpRequest request, HttpResponse response) {
        return interceptors.entrySet().stream()
                           .filter(interceptorEntry -> interceptorEntry.getKey().contains(request.getRequestLine()))
                           .allMatch(interceptorEntry -> interceptorEntry.getValue().preHandle(request, response));
    }

    private Controller findController(HttpRequest request) {
        return controllers.stream()
                          .filter(candidate -> candidate.canHandle(request))
                          .findAny()
                          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경로입니다."));
    }
}
