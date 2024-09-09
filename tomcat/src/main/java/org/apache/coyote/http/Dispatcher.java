package org.apache.coyote.http;

import com.techcourse.controller.BasicController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.Path;
import org.apache.coyote.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dispatcher {

    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private static final Map<String, Controller> controllers = new ConcurrentHashMap<>();

    public Dispatcher() {
        controllers.put("/", new BasicController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public String dispatch(HttpRequest request) {
        Controller controller = getController(request.getPath());
        try {
            return controller.service(request).toResponse();
        } catch (NullPointerException e) {
            return HttpResponse.notFoundResponses().toResponse();
        } catch (Exception e) {
            return HttpResponse.serverErrorResponses().toResponse();
        }
    }

    private Controller getController(Path path) {
        if (path.isResourceUri()) {
            return new StaticResourceController();
        }
        Controller controller = controllers.get(path.getUri());
        if (controller == null) {
            throw new IllegalArgumentException("Controller not found");
        }
        return controller;
    }
}
