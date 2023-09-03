package nextstep.jwp.handler;

import nextstep.jwp.handler.mappaing.HandlerMapping;
import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.handler.RequestHandler;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;

public class DispatcherServlet implements Servlet {

    private final HandlerMapping mapping;

    public DispatcherServlet(HandlerMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        RequestHandler handler = mapping.getHandler(request);
        // TODO null 처리
        return handler.handle(request);
    }
}
