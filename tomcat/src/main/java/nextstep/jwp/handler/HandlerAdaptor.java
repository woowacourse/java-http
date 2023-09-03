package nextstep.jwp.handler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class HandlerAdaptor {

    private static final Map<String, RequestHandler> handlers = new ConcurrentHashMap<>();

    static {
        handlers.put("/index", IndexHandler.getInstance());
        handlers.put("/login", LoginHandler.getInstance());
        handlers.put("/register", RegisterHandler.getInstance());
    }

    private HandlerAdaptor() {
    }

    public static HttpResponse handle(HttpRequest request) throws IOException {
        RequestHandler handler = extractHandler(request.getNativePath());

        return handler.handle(request);
    }

    private static RequestHandler extractHandler(String nativePath) {
        if ("/".equals(nativePath)) {
            return HomeHandler.getInstance();
        }

        return handlers.keySet()
                .stream()
                .filter(nativePath::startsWith)
                .findAny()
                .map(handlers::get)
                .orElseGet(ResourceHandler::getInstance);
    }
}
