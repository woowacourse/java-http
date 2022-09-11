package org.apache.coyote.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.presentation.DefaultController;
import nextstep.jwp.presentation.HomeController;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.UserController;
import org.apache.coyote.http11.http.HttpPath;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new ConcurrentHashMap<>();

    private static final Controller DEFAULT = new DefaultController();
    private static final Controller RESOURCE = new ResourceController();

    static {
        controllers.put("/index", new HomeController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new UserController());
    }

    public static Controller getController(final HttpPath path) {
        if (path.isDefault()) {
            return DEFAULT;
        }

        if (path.isDefaultResource()) {
            return RESOURCE;
        }
        return controllers.getOrDefault(path.getDomainPath(), notFound());
    }

    private static Controller notFound() {
        return new NotFoundController();
    }
}
