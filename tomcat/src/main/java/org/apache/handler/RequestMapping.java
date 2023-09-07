package org.apache.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, AbstractController> handlers = new HashMap<>();
    private static final String QUERY_STRING = "?";

    static {
        handlers.put("/", new HomeController());
        handlers.put("/login", new LoginController());
        handlers.put("/register", new ResisterController());
    }

    public AbstractController findController(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        if (isQueryString(path)) {
            path = extractPathWithoutQuery(path);
        }

        return handlers.getOrDefault(path, new FileController());
    }

    private boolean isQueryString(String path) {
        return path.contains(QUERY_STRING);
    }

    private String extractPathWithoutQuery(String path) {
        int index = path.indexOf(QUERY_STRING);
        path = path.substring(0, index);
        return path;
    }
}
