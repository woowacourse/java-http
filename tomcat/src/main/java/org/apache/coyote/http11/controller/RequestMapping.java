package org.apache.coyote.http11.controller;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.IndexController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.ResourceController;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {

    public static final String FILE_EXTENSION_DELIMITER = ".";
    private static final Map<String, Controller> mapper = new HashMap<>();
    private static final SessionManager sessionManager = SessionManager.getSessionManager();

    static {
        mapper.put("/", new HomeController());
        mapper.put("/index", new IndexController());
        mapper.put("/login", new LoginController(sessionManager));
        mapper.put("/register", new RegisterController(sessionManager));
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        int extensionIndex = path.indexOf(FILE_EXTENSION_DELIMITER);

        if (extensionIndex == -1) {
            validatePath(path);
            return mapper.get(path);
        }

        String pathWithoutExtension = path.substring(0, extensionIndex);
        if (mapper.containsKey(pathWithoutExtension)) {
            return mapper.get(pathWithoutExtension);
        }
        return new ResourceController();
    }

    private void validatePath(String path) {
        if (!mapper.containsKey(path)) {
            throw new IllegalArgumentException("invalid path: " + path);
        }
    }
}
