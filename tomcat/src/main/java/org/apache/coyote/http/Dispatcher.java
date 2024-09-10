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

    public void dispatch(HttpRequest request, HttpResponse response) {
        try {
            Controller controller = getController(request.getPath());
            log.info("controller: {}", controller.getClass());
            controller.service(request, response);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            response.notFoundResponses();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.serverErrorResponses();
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
