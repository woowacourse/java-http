package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.request.RequestURI;
import org.apache.coyote.http11.message.response.Response;

public class Handlers {

    private static final Map<String, Handler> mappings;

    static {
        mappings = new HashMap<>();
        mappings.put("/", new DefaultHandler());
        mappings.put("/index", new IndexHandler());
        mappings.put("/login", new LoginHandler());
        mappings.put("/register", new RegisterHandler());
    }

    private Handlers() {
    }

    public static Response handle(Request request) throws IOException {
        RequestURI requestURI = request.getRequestURI();
        String absolutePathWithoutExtension = removeExtension(requestURI.absolutePath());

        Handler handler = mappings.entrySet().stream()
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
