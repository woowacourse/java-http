package nextstep.servlet;

import java.util.List;
import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.servlet.interceptor.Interceptor;
import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.ServletManger;
import org.apache.coyote.http11.message.request.RequestLine;

public class DispatcherServletManager implements ServletManger {

    private final Map<List<RequestLine>, Interceptor> interceptors;
    private final List<Controller> controllers;
    private final StaticResourceResolver staticResourceResolver;

    public DispatcherServletManager(
            Map<List<RequestLine>, Interceptor> interceptors,
            List<Controller> controllers,
            StaticResourceResolver staticResourceResolver
    ) {
        this.interceptors = interceptors;
        this.controllers = controllers;
        this.staticResourceResolver = staticResourceResolver;
    }

    @Override
    public Servlet createServlet() {
        return new DispatcherServlet(
                interceptors,
                controllers,
                staticResourceResolver
        );
    }
}
