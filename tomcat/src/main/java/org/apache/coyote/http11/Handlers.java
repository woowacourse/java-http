package org.apache.coyote.http11;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Handlers {

    private static final Map<String, Handler> handlers;

    static {
        handlers = new HashMap<>();
        handlers.put("/", new DefaultHandler());
        handlers.put("/index", new IndexHandler());
        handlers.put("/login", new LoginHandler());
        handlers.put("/register", new RegisterHandler());
    }

    public static Response handle(Request request) throws IOException {
        RequestURI requestURI = request.getRequestURI();
        String absolutePathWithoutExtension = removeExtension(requestURI.absolutePath());

        Handler handler = handlers.entrySet().stream()
                .filter(entry -> entry.getKey().equals(absolutePathWithoutExtension))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseGet(StaticResourceHandler::new);

        return handler.handle(request);
    }


    private static String removeExtension(String target) {
        if (target.contains(".")) {
            return target.substring(0, target.indexOf("."));
        }
        return target;
    }
}
