package org.apache.catalina;

import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

import java.io.IOException;
import java.util.Map;

public class HandlerAdapter {

    private final Map<String, Controller> handler = Map.of(
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        String url = httpRequest.getUrl();
        Controller controller = handler.getOrDefault(url, new StaticResourceController());

        return controller.execute(httpRequest);
    }
}
