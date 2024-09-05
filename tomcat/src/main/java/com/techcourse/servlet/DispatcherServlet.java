package com.techcourse.servlet;

import com.techcourse.servlet.handler.HomePageHandler;
import com.techcourse.servlet.handler.LoginPageHandler;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.servlet.Servlet;
import org.apache.coyote.http11.response.view.View;

public class DispatcherServlet implements Servlet {
    private static final List<Handler> APPLICATION_HANDLERS = List.of(new HomePageHandler(), new LoginPageHandler());

    private final List<Handler> handlers;

    public DispatcherServlet() {
        this.handlers = APPLICATION_HANDLERS;
    }

    public DispatcherServlet(List<Handler> handlers) {
        this.handlers = handlers;
    }

    public Optional<View> service(HttpRequest request) {
        return handlers.stream()
                .filter(handler -> handler.support(request))
                .map(handler -> handler.handle(request))
                .findFirst();
    }
}
