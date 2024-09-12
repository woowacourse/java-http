package com.techcourse.servlet;

import com.techcourse.controller.Controller;
import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.servlet.Servlet;

public class DispatcherServlet implements Servlet {
    private static final Map<String, Controller> APPLICATION_HANDLER_MAPPER = Map.ofEntries(
            Map.entry("/", new HomeController()), Map.entry("/login", new LoginController()),
            Map.entry("/register", new RegisterController()));

    private final Map<String, Controller> handlerMapper;

    public DispatcherServlet() {
        this.handlerMapper = APPLICATION_HANDLER_MAPPER;
    }

    public DispatcherServlet(Map<String, Controller> handlerMapper) {
        this.handlerMapper = handlerMapper;
    }

    @Override
    public boolean canService(HttpRequest request) {
        return handlerMapper.containsKey(request.getPath());
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        handlerMapper.entrySet().stream()
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
