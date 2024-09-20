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

    private static final Map<String, Controller> controllers = new ConcurrentHashMap<>();
    private static final StaticController staticController = new StaticController();

    static {
        controllers.put("/", new IndexController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public static Controller getController(HttpRequest request) {
        String path = request.getPath();
        return controllers.getOrDefault(path, staticController);
    }

    private RequestMapping() {
    }
}
