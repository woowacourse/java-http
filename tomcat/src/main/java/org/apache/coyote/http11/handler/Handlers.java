package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

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

    public static void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String requestUri = httpRequest.getEndPoint();
        Handler handler = findHandler(requestUri);
        handler.handle(httpRequest, httpResponse);
    }

    private static Handler findHandler(String requestUri) {
        return myHandlers.getOrDefault(requestUri, fileHandler);
    }
}
