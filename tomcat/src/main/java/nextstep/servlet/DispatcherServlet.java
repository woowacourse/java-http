package nextstep.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.servlet.interceptor.Interceptor;
import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestLine;
import org.apache.coyote.http11.message.response.HttpResponse;

public class DispatcherServlet implements Servlet {

    private final Map<List<RequestLine>, Interceptor> interceptors;
    private final List<Controller> controllers;
    private final StaticResourceResolver staticResourceResolver;

    public DispatcherServlet(
            final Map<List<RequestLine>, Interceptor> interceptors,
            final List<Controller> controllers,
            final StaticResourceResolver staticResourceResolver
    ) {
        this.interceptors = interceptors;
        this.controllers = controllers;
        this.staticResourceResolver = staticResourceResolver;
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
