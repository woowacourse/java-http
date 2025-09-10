package com.spring.servlet;

import com.spring.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.spring.http.enums.HttpStatus;
import com.techcourse.exception.HttpStatusException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.spring.http.request.HttpRequest;
import com.spring.http.response.HttpResponse;
import org.apache.catalina.servlet.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherServlet implements HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private final Map<String, Controller> controllers = new ConcurrentHashMap<>();

    public DispatcherServlet() {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        final String requestPath = request.requestStartLine().path();
        log.debug("request path = {}", requestPath);

        final Controller controller = controllers.get(requestPath);
        if (controller == null) {
            throw new HttpStatusException("No controller found for path: " + requestPath, HttpStatus.NOT_FOUND);
        }
        controller.service(request, response);
    }
}
