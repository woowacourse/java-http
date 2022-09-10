package org.apache.catalina.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class RequestMapping {

    private final Map<String, Controller> controllers = new HashMap<>();

    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        Controller controller = findController(request.getPath());
        controller.service(request, response);
    }

    private Controller findController(final String path) {
        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }
        return new ResourceController();
    }

    public void addController(final String path, final Controller controller) {
        controllers.put(path, controller);
    }
}
