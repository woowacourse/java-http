package nextstep.jwp.servlet;

import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DispatcherServlet implements Servlet {

    private final ControllerAdvice controllerAdvice;
    private final RequestMapper requestMapper;

    public DispatcherServlet(ControllerAdvice controllerAdvice, RequestMapper requestMapper) {
        this.controllerAdvice = controllerAdvice;
        this.requestMapper = requestMapper;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            requestMapper.handle(httpRequest, httpResponse);
        } catch (Exception e) {
            controllerAdvice.handleException(e, httpResponse);
        }
    }
}
