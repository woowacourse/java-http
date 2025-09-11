package org.apache.catalina.mapping;

import com.controller.Controller;
import com.controller.LoginController;
import com.controller.RegisterController;
import com.controller.RootController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class DynamicControllerMapping {

    private final Map<RequestKey, Controller> controllers = new HashMap<>();

    public DynamicControllerMapping() {
        register("/", HttpMethod.GET, new RootController());
        register("/login", HttpMethod.POST, new LoginController());
        register("/login", HttpMethod.GET, new LoginController());
        register("/register", HttpMethod.POST, new RegisterController());
        register("/register", HttpMethod.GET, new RegisterController());
    }

    private void register(final String uri, final HttpMethod method, final Controller controller) {
        controllers.put(new RequestKey(uri, method), controller);
    }

    public Controller findController(final String uri, final HttpMethod httpMethod) {
        final RequestKey key = new RequestKey(uri, httpMethod);

        return controllers.get(key);
    }
}
