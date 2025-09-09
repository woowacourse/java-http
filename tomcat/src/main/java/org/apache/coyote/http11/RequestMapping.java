package org.apache.coyote.http11;

import com.techcourse.controller.DefaultController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.controller.UserRegisterController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.constant.ResourcePath;

public class RequestMapping {

    private static final Map<String, Controller> controllerMap = new HashMap<>();

    static {
        put(new LoginController());
        put(new DefaultController());
        put(new StaticResourceController());
        put(new UserRegisterController());
        put(new NotFoundController());
    }

    public static void put(AbstractController controller) {
        controllerMap.put(controller.getUrl(), controller);
    }

    public static Controller getController(HttpRequest httpRequest) {
        final ResourcePath resourcePath = httpRequest.getRequestLine().resourcePath();
        if (resourcePath.isStaticResource()) {
            return controllerMap.get("static");
        }
        if (!controllerMap.containsKey(resourcePath.value())) {
            return controllerMap.get("notFound");
        }
        return controllerMap.get(resourcePath.value());
    }
}
