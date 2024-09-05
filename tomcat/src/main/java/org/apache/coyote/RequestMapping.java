package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.coyote.controller.FrontController;
import org.apache.coyote.controller.HomeController;
import org.apache.coyote.controller.RegisterController;
import org.apache.coyote.controller.StaticResourceController;
import org.apache.coyote.http11.LoginController;

public class RequestMapping {

    private static final Pattern LOGIN_REGEX = Pattern.compile("^(/login)(\\?([^#\\s]*))?");
    private static final Pattern STATIC_RESOURCE_PATTERN = Pattern.compile("\\.(html|css|js|jpg|jpeg|png|gif|ico|svg|woff|woff2|ttf|eot)$", Pattern.CASE_INSENSITIVE);

    private static final Map<String, FrontController> controllers = new HashMap<>();

    static {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
        controllers.put("/", new HomeController());
        controllers.put("/static", new StaticResourceController());
    }

    private RequestMapping() {
    }

    public static FrontController getController(String path) {
        if (LOGIN_REGEX.matcher(path).matches()) {
            return controllers.get("/login");
        }
        if (STATIC_RESOURCE_PATTERN.matcher(path).find()) {
            return controllers.get("/static");
        }
        return controllers.get(path);
    }
}
