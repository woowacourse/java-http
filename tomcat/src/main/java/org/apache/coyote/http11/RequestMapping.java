package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.IndexController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticController;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.message.request.HttpRequest;

public class RequestMapping {

    private static class RequestMappingHolder {
        private static final RequestMapping INSTANCE = new RequestMapping();
    }

    public static RequestMapping getInstance() {
        return RequestMappingHolder.INSTANCE;
    }

    private final Map<String, Controller> controllers = new ConcurrentHashMap<>();
    private final StaticController staticController = new StaticController();

    private RequestMapping() {
        controllers.put("/", new IndexController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        return controllers.getOrDefault(path, staticController);
    }
}
