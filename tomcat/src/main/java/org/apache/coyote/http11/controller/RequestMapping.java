package org.apache.coyote.http11.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.techcourse.controller.HomePageController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final RequestMapping instance = new RequestMapping();
    private static final Map<String, Controller> controllers = new ConcurrentHashMap<>();
    private static final Controller STATIC_RESOURCE_CONTROLLER = StaticResourceController.getInstance();
    private static final Controller HOME_PAGE_CONTROLLER = HomePageController.getInstance();
    private static final String PATH_SEPARATOR = "/";
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    static {
        controllers.put("/login", LoginController.getInstance());
        controllers.put("/register", RegisterController.getInstance());
    }

    private RequestMapping() {
    }

    public static RequestMapping getInstance() {
        return instance;
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        if (PATH_SEPARATOR.equals(path)) {
            return HOME_PAGE_CONTROLLER;
        }
        Controller controller;
        if (path.contains(FILE_EXTENSION_SEPARATOR) || (controller = controllers.get(path)) == null) {
            return STATIC_RESOURCE_CONTROLLER;
        }
        return controller;
    }
}
