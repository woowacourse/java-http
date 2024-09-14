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

    private final RequestMapping requestMapping;

    public DispatcherServlet() {
        this.requestMapping = new RequestMapping(APPLICATION_HANDLER_MAPPER);
    }

    public DispatcherServlet(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public boolean canService(HttpRequest request) {
        return requestMapping.getController(request).isPresent();
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        requestMapping.getController(request)
                .ifPresent(controller -> doService(request, response, controller));
    }

    private void doService(HttpRequest request, HttpResponse response, Controller controller) {
        try {
            controller.service(request, response);
        } catch (Exception e) {
            response.sendStaticResource(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
        }
    }
}
