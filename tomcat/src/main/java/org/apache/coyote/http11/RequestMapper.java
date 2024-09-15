package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticPageController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapper {

    private static final Map<String, Controller> controllers = new HashMap<>();

    private RequestMapper() {
        controllers.put("/login", LoginController.getInstance());
        controllers.put("/register", RegisterController.getInstance());
        controllers.put("/", StaticPageController.getInstance());
    }

    public static Controller mapRequest(HttpRequest request) {
        return controllers.getOrDefault(request.getHttpRequestPath(), StaticPageController.getInstance());
    }
}
