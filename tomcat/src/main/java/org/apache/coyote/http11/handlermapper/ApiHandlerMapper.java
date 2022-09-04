package org.apache.coyote.http11.handlermapper;

import java.util.List;
import org.apache.coyote.http11.handler.ApiHandler.LoginApiHandler;
import org.apache.coyote.http11.handler.ApiHandler.RegisterApiHandler;
import org.apache.coyote.http11.handler.ApiHandler.RootApiHandler;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;

public class ApiHandlerMapper implements HandlerMapper{

    private static final List<Handler> API_HANDLERS = List.of(new RootApiHandler(), new LoginApiHandler(), new RegisterApiHandler());

    @Override
    public Handler mapHandler(HttpRequest httpRequest) {
        return API_HANDLERS.stream()
                .filter(apiHandler -> apiHandler.canHandle(httpRequest))
                .findFirst()
                .orElse(null);
    }
}

