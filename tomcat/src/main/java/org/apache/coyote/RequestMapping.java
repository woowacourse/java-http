package org.apache.coyote;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;
import com.techcourse.controller.StaticResourceController;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.handler.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new ConcurrentHashMap<>();

    public RequestMapping() {
        controllers.put("/", new RootController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public void doService(HttpRequest request, HttpResponse response) throws Exception {
        Controller controller = selectController(request);

        controller.service(request, response);
    }

    private Controller selectController(HttpRequest request) {
        if (request.isResource()) {
            return new StaticResourceController();
        }
        Controller controller = controllers.get(request.getUrl());
        if (controller == null) {
            return new NotFoundController();
        }
        return controller;
    }
}
