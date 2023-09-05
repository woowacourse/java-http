package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestURI;
import org.apache.coyote.http11.message.response.HttpResponse;

public class Handlers {

    private static final Map<String, Handler> mappings;

    static {
        mappings = new HashMap<>();
        mappings.put("/", new RootHandler());
        mappings.put("/index", new IndexHandler());
        mappings.put("/login", new LoginHandler());
        mappings.put("/register", new RegisterHandler());
    }

    private Handlers() {
    }

    public static HttpResponse handle(HttpRequest httpRequest) throws IOException {
        RequestURI requestURI = httpRequest.getRequestURI();
        String absolutePath = requestURI.absolutePath();

        Handler handler = mappings.entrySet().stream()
                .filter(entry -> entry.getKey().equals(absolutePath))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseGet(StaticResourceHandler::new);

        return handler.handle(httpRequest);
    }
}
