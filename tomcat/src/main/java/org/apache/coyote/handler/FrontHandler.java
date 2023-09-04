package org.apache.coyote.handler;

import org.apache.coyote.handler.mapping.HandlerMapping;
import org.apache.coyote.handler.mapping.StaticFileMapping;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FrontHandler {

    private static final Set<HandlerMapping> handlerMapping = new HashSet<>();

    static {
        handlerMapping.add(new StaticFileMapping());
    }

    public String handle(final String firstLine, final Map<String, String> headers, final String requestBody) throws IOException {
        String response = "";
        final String[] parsedFirstLine = firstLine.split(" ");
        for (final HandlerMapping mapping : handlerMapping) {
            final String requestUri = parsedFirstLine[1];
            if (mapping.supports(requestUri)) {
                response = mapping.handle(parsedFirstLine[1]);
                break;
            }
        }

        return response;
    }
}
