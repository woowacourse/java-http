package org.apache.coyote;

import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestUri;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestMapping {

    private static Map<String, Controller> mappers = new LinkedHashMap<>();

    static {
        mappers.put("/", new RootController());
        mappers.put("/login", new LoginController());
        mappers.put("/register", new RegisterController());
    }


    public Controller getController(HttpRequest request) {
        RequestUri requestUri = request.getRequestLine().getRequestUri();

        String path = requestUri.getPath();

        return mappers.entrySet()
                .stream()
                .filter(entry -> path.equals(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(new DefaultController());
    }
}
