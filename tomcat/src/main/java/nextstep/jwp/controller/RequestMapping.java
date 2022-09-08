package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.HttpRequest;
import org.apache.controller.Controller;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new HelloWorldController());
    }

    public static Controller get(HttpRequest httpRequest) {
        String uri = httpRequest.getUri();
        return controllers.get(uri);
    }
}
