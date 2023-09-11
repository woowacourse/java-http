package org.apache.coyote.handlermapping;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.dynamichandler.HelloHandler;
import org.apache.coyote.dynamichandler.LoginHandler;
import org.apache.coyote.dynamichandler.RegisterHandler;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.statichandler.ExceptionHandler;
import org.apache.coyote.statichandler.StaticHandler;

public class HandlerMapper {

    private static final Map<String, Handler> handlers = new ConcurrentHashMap<>(
            Map.ofEntries(
                    new AbstractMap.SimpleEntry<String, Handler>("/", new HelloHandler()),
                    new AbstractMap.SimpleEntry<String, Handler>("/login", new LoginHandler()),
                    new AbstractMap.SimpleEntry<String, Handler>("/register", new RegisterHandler())
            )
    );

    private HandlerMapper(){
    }

    public static Handler getHandle(HttpRequest httpRequest) {
        if (handlers.containsKey(httpRequest.getPath())) {
            return handlers.get(httpRequest.getPath());
        }

        if (canHandleStaticPath(httpRequest.getPath())) {
            return new StaticHandler();
        }

        return new ExceptionHandler(HttpStatus.NOT_FOUND);
    }

    private static boolean canHandleStaticPath(String path) {
        return ContentType.isExistsExtension(path);
    }

}
