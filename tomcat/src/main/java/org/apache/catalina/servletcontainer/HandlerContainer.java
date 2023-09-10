package org.apache.catalina.servletcontainer;

import nextstep.jwp.handler.FileHandler;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.handler.RegisterHandler;
import nextstep.jwp.handler.RootHandler;
import org.apache.coyote.http11.Container;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;

public class HandlerContainer implements Container {

    private static final Handler fileHandler = new FileHandler();

    private static final Map<String, Handler> myHandlers =
            Map.of(
                    "/", new RootHandler(),
                    "/login", new LoginHandler(),
                    "/register", new RegisterHandler()
            );

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String requestUri = httpRequest.getEndPoint();
        Handler handler = findHandler(requestUri);
        handler.handle(httpRequest, httpResponse);
    }

    private Handler findHandler(String requestUri) {
        return myHandlers.getOrDefault(requestUri, fileHandler);
    }
}
