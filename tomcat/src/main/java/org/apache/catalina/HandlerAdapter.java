package org.apache.catalina;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.HelloWorldController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

import java.util.Map;

public class HandlerAdapter {

    private final Map<String, Controller> mapper = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController(),
            "/", new HelloWorldController()
    );

    public void handle(HttpRequest request, HttpResponse response) {
        String uri = request.getUri();

        mapper.getOrDefault(uri, new StaticResourceController())
                .service(request, response);
    }
}
