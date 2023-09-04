package org.apache.coyote.handler;

import org.apache.coyote.handler.mapping.HandlerMapping;
import org.apache.coyote.handler.mapping.LoginMapping;
import org.apache.coyote.handler.mapping.LoginPageMapping;
import org.apache.coyote.handler.mapping.RegisterMapping;
import org.apache.coyote.handler.mapping.RegisterPageMapping;
import org.apache.coyote.handler.mapping.StaticFileMapping;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FrontHandler {

    private static final Set<HandlerMapping> handlerMapping = new HashSet<>();

    static {
        handlerMapping.add(new StaticFileMapping());
        handlerMapping.add(new LoginMapping());
        handlerMapping.add(new LoginPageMapping());
        handlerMapping.add(new RegisterMapping());
        handlerMapping.add(new RegisterPageMapping());
    }

    public String handle(final String firstLine, final Map<String, String> headers, final String requestBody) throws IOException {
        String response = "";
        final String[] parsedFirstLine = firstLine.split(" ");
        for (final HandlerMapping mapping : handlerMapping) {
            final String httpMethod = parsedFirstLine[0];
            final String requestUri = parsedFirstLine[1];
            if (mapping.supports(httpMethod, requestUri)) {
                response = mapping.handle(parsedFirstLine[1], headers, requestBody);
                break;
            }
        }

        return response;
    }
}
