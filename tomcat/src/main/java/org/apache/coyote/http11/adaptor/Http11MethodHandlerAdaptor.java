package org.apache.coyote.http11.adaptor;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.apache.coyote.header.HttpMethod;
import org.apache.coyote.http11.handler.Http11MethodHandler;

public class Http11MethodHandlerAdaptor {

    private final Map<HttpMethod, Http11MethodHandler> handlers;

    public Http11MethodHandlerAdaptor(final Set<Http11MethodHandler> handlers) {
        this.handlers = handlers.stream()
                .collect(toMap(Http11MethodHandler::supportMethod, identity()));
    }

    public String handle(final HttpMethod httpMethod, String request) throws IOException {
        Http11MethodHandler handler = handlers.get(httpMethod);
        return handler.handle(request);
    }
}
