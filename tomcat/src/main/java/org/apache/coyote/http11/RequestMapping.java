package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

// URL 경로와 컨트롤러를 각각 매핑해둔 클래스
public class RequestMapping {

    private static final Map<String, Controller> controllerMap = new HashMap<>();

    static {
        controllerMap.put("/login", new LoginController());
        controllerMap.put("/register", new RegisterController());
    }

    public static Controller getController(String path) {
        return controllerMap.get(path);
    }
}
