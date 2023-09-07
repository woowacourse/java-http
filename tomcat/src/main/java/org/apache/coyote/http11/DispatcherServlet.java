package org.apache.coyote.http11;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class DispatcherServlet {

    private DispatcherServlet() {
    }

    public static Response service(final Request request) {
        final var handler = RequestHandlerMapping.getHandler(request);

        return handler.service(request);
    }

}
