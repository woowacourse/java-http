package org.apache.catalina.servlet;

import java.util.HashMap;
import java.util.Map;
import org.apache.tomcat.util.http.ResourceURI;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootController;

public class RequestMapping {

    private static final Map<ResourceURI, Controller> routes = new HashMap<>();
    private static final DefaultServlet DEFAULT_SERVLET = new DefaultServlet();

    static {
        routes.put(new ResourceURI("/login"), new LoginController());
        routes.put(new ResourceURI("/register"), new RegisterController());
        routes.put(new ResourceURI("/"), new RootController());
    }

    private RequestMapping() {
    }

    public static Controller getController(ResourceURI uri) {
        return routes.getOrDefault(uri, DEFAULT_SERVLET);
    }
}
