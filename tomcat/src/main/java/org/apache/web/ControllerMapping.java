package org.apache.web;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class ControllerMapping {

    private final Map<RequestKey, Controller> controllers = new HashMap<>();

    public ControllerMapping() {
        //Dynamic
        register("/login", HttpMethod.POST, new LoginController());
        register("/login", HttpMethod.GET, new LoginController());
        register("/register", HttpMethod.POST, new RegisterController());

        //html
        register("/", HttpMethod.GET, new StaticResourcesController());
        register("/register", HttpMethod.GET, new StaticResourcesController());
        register("/401.html", HttpMethod.GET, new StaticResourcesController());
        register("/404.html", HttpMethod.GET, new StaticResourcesController());
        register("/index.html", HttpMethod.GET, new StaticResourcesController());

        //js
        register("/js/scripts.js", HttpMethod.GET, new StaticResourcesController());
        register("/assets/chart-area.js", HttpMethod.GET, new StaticResourcesController());
        register("/assets/chart-bar.js", HttpMethod.GET, new StaticResourcesController());
        register("/assets/chart-pie.js", HttpMethod.GET, new StaticResourcesController());

        //css
        register("/css/styles.css", HttpMethod.GET, new StaticResourcesController());
    }

    private void register(final String uri, final HttpMethod method, final Controller controller) {
        controllers.put(new RequestKey(uri, method), controller);
    }

    public Controller findController(final String uri, final HttpMethod httpMethod) {
        final RequestKey key = new RequestKey(uri, httpMethod);

        return controllers.get(key);
    }
}
