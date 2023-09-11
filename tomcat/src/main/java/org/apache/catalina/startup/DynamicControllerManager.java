package org.apache.catalina.startup;

import common.http.Controller;
import common.http.ControllerManager;
import common.http.Request;
import common.http.Response;

import java.util.HashMap;
import java.util.Map;

public class DynamicControllerManager implements ControllerManager {

    private static final Map<String, Controller> mapper = new HashMap<>();

    @Override
    public void add(String path, Controller controller) {
        mapper.put(path, controller);
    }

    @Override
    public void service(Request request, Response response) {
        Controller controller = mapper.get(request.getPath());
        if (controller == null) {
            return;
        }

        controller.service(request, response);
    }
}
