package org.apache.catalina.handle;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.data.HttpRequest;

public class RequestHandlerResolver {

    private final List<RequestHandler> requestHandlers = new ArrayList<>();


    public void registerLast(RequestHandler requestHandler) {
        requestHandlers.add(requestHandler);
    }

    public RequestHandler resolve(HttpRequest request) {
        for (RequestHandler requestHandler : requestHandlers) {
            if (requestHandler.canHandle(request)) {
                return requestHandler;
            }
        }

        return null;
    }
}
