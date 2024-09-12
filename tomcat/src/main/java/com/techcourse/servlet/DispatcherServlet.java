package com.techcourse.servlet;

import com.techcourse.controller.Controller;
import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.servlet.handler.HomePageHandler;
import com.techcourse.servlet.handler.LoginHandler;
import com.techcourse.servlet.handler.LoginPageHandler;
import com.techcourse.servlet.handler.RegisterHandler;
import com.techcourse.servlet.handler.RegisterPageHandler;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.view.View;
import org.apache.coyote.http11.servlet.Servlet;

public class DispatcherServlet implements Servlet {

    private static final Map<String, Controller> HANDLER_MAPPER = Map.ofEntries(
            Map.entry("/", new HomeController()), Map.entry("/login", new LoginController()),
            Map.entry("/register", new RegisterController()));
    private static final List<Handler> APPLICATION_HANDLERS = List.of(
            new HomePageHandler(), new LoginPageHandler(), new LoginHandler(), new RegisterPageHandler(),
            new RegisterHandler());

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

    @Override
    public boolean canService(HttpRequest request) {
        return HANDLER_MAPPER.containsKey(request.getPath());
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HANDLER_MAPPER.entrySet().stream()
                .filter(entry -> entry.getKey().equals(request.getPath()))
                .findFirst()
                .ifPresent(entry -> doService(request, response, entry.getValue()));
    }

    private void doService(HttpRequest request, HttpResponse response, Controller controller) {
        try {
            controller.service(request, response);
        } catch (Exception e) {
            response.sendStaticResource(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
        }
    }
}
