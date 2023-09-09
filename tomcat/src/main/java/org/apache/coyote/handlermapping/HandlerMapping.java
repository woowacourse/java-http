package org.apache.coyote.handlermapping;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.dynamichandler.LoginHandler;
import org.apache.coyote.handler.dynamichandler.RegisterHandler;
import org.apache.coyote.handler.statichandler.ExceptionHandler;
import org.apache.coyote.handler.dynamichandler.HelloHandler;
import org.apache.coyote.handler.statichandler.StaticHandler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMapping {

    private static final Map<String, Handler> handlers = new ConcurrentHashMap<>(
            Map.ofEntries(
                    new AbstractMap.SimpleEntry<String, Handler>("/", new HelloHandler()),
                    new AbstractMap.SimpleEntry<String, Handler>("/login", new LoginHandler()),
                    new AbstractMap.SimpleEntry<String, Handler>("/register", new RegisterHandler())
            )
    );

    public static Handler getHandler(String requestTarget) {
        if (canHandleByStaticHandler(requestTarget)) {
            return new StaticHandler();
        }
        if (handlers.containsKey(requestTarget)) {
            return handlers.get(requestTarget);
        }
        return new ExceptionHandler(HttpStatus.NOT_FOUND);
    }

    private static boolean canHandleByStaticHandler(String requestTarget) {
        return Arrays.stream(ContentType.values())
                .map(ContentType::extension)
                .anyMatch(requestTarget::endsWith);
    }
}
