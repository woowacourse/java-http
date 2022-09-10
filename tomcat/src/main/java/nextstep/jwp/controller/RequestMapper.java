package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.handler.Controller;
import org.apache.coyote.handler.RequestMapping;
import org.apache.coyote.handler.ResourceHandler;

public class RequestMapper implements RequestMapping {

    private static final Map<String, Controller> requestMap = new HashMap<>();

    static {
        requestMap.put("/", new HomeController());
        requestMap.put("/login", new LoginController());
        requestMap.put("/register", new RegisterController());
    }

    @Override
    public Controller find(final String requestUri) {
        Controller controller = requestMap.get(requestUri);
        if (controller == null) {
            return new ResourceHandler();
        }
        return controller;
    }
}
