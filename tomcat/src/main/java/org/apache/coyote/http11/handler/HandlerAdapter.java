package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import nextstep.jwp.controller.HelloWorldController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ViewController;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.exception.NoSuchApiException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;

public class HandlerAdapter {
    private final Map<RequestMapper, Controller> controllers = new HashMap<>();

    public HandlerAdapter() {
        addController(HttpMethod.POST, "/login", new LoginController());
        addController(HttpMethod.POST, "/register", new RegisterController());
        addController(HttpMethod.GET, "/login", new LoginController());
        addController(HttpMethod.GET, "/register", new RegisterController());
        addController(HttpMethod.GET, "/", new HelloWorldController());
    }

    public Controller mapping(Request request) {
        RequestMapper requestInfo
                = new RequestMapper(request.getMethod(), request.getPath());
        if (request.getPath().contains(".")) {
            return new ViewController();
        }
        return controllers.entrySet().stream()
                .filter(entry -> entry.getKey().equals(requestInfo))
                .findFirst()
                .map(Entry::getValue)
                .orElseThrow(NoSuchApiException::new);
    }

    private void addController(HttpMethod httpMethod, String path, Controller controller) {
        controllers.put(new RequestMapper(httpMethod, path), controller);
    }
}
