package org.apache.catalina;

import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

import java.util.Map;

public class HandlerAdapter {

    private final Map<String, Controller> mapper = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public HttpResponse handle(HttpRequest request) {
        String uri = request.getUri();

        HttpResponse response = new HttpResponse();
        mapper.getOrDefault(uri, new StaticResourceController())
                .service(request, response);

        return response;
    }
}
