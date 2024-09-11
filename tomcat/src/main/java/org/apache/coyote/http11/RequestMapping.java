package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import com.techcourse.controller.Controller;
import com.techcourse.controller.IndexController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new IndexController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public static Controller getController(HttpRequest request) throws Exception {
        return controllers.get(request.getPath());
    }
}
