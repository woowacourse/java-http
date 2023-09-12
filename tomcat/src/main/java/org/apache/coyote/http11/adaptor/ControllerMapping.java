package org.apache.coyote.http11.adaptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.handler.Controller;

public class ControllerMapping {

    private static final Map<String, Controller> mapper = new ConcurrentHashMap<>();

    static {
        mapper.put("/", new HelloController());
        mapper.put("/login", new LoginController());
        mapper.put("/register", new RegisterController());
    }

    public void handle(HttpRequest request, HttpResponse response) throws Exception {
        String requestUrlWithoutQueryParams = request.getRequestUrlWithoutQueryParams();
        Controller controller = mapper.getOrDefault(requestUrlWithoutQueryParams, new ResourceController());
        controller.service(request, response);
    }
}
