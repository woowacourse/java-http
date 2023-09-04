package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.ResponseEntity;

import java.io.IOException;
import java.util.Map;

public class Handlers {

    private static final Handler fileHandler = new FileHandler();

    private static final Map<String, Handler> myHandlers =
            Map.of(
                    "/", new RootHandler(),
                    "/login", new LoginHandler(),
                    "/register", new RegisterHandler()
            );

    private Handlers() {

    }

    public static ResponseEntity handle(HttpRequest request) throws IOException {
        String requestUri = request.getEndPoint();
        Handler handler = findHandler(requestUri);
        return handler.handle(request);
    }

    private static Handler findHandler(String requestUri) {
        return myHandlers.getOrDefault(requestUri, fileHandler);
    }
}
