package org.apache.coyote.handler;

import org.apache.coyote.handler.mapping.HandlerMapping;
import org.apache.coyote.handler.mapping.LoginMapping;
import org.apache.coyote.handler.mapping.LoginPageMapping;
import org.apache.coyote.handler.mapping.RegisterMapping;
import org.apache.coyote.handler.mapping.RegisterPageMapping;
import org.apache.coyote.handler.mapping.StaticFileMapping;
import org.apache.coyote.http.HttpRequest;

import java.io.IOException;
import java.util.HashSet;
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

    public String handle(final HttpRequest httpRequest) throws IOException {
        String response = "";
        for (final HandlerMapping mapping : handlerMapping) {
            if (mapping.supports(httpRequest)) {
                response = mapping.handle(httpRequest);
                break;
            }
        }

        return response;
    }
}
