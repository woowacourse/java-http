package org.apache.coyote.handlermapping;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.dynamichandler.LoginHandler;
import org.apache.coyote.handler.dynamichandler.RegisterHandler;
import org.apache.coyote.handler.statichandler.*;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMapping {

    private static Map<HandlerMatcher, Handler> handlerMap = new ConcurrentHashMap<>(
            Map.ofEntries(
                    new AbstractMap.SimpleEntry<HandlerMatcher, Handler>(new HandlerMatcher(HttpMethod.GET, "/"), new HelloHandler()),
                    new AbstractMap.SimpleEntry<HandlerMatcher, Handler>(new HandlerMatcher(HttpMethod.GET, "/index.html"), new HtmlHandler()),
                    new AbstractMap.SimpleEntry<HandlerMatcher, Handler>(new HandlerMatcher(HttpMethod.GET, "/assets/chart-area.js"), new JsHandler()),
                    new AbstractMap.SimpleEntry<HandlerMatcher, Handler>(new HandlerMatcher(HttpMethod.GET, "/assets/chart-bar.js"), new JsHandler()),
                    new AbstractMap.SimpleEntry<HandlerMatcher, Handler>(new HandlerMatcher(HttpMethod.GET, "/assets/chart-pie.js"), new JsHandler()),
                    new AbstractMap.SimpleEntry<HandlerMatcher, Handler>(new HandlerMatcher(HttpMethod.GET, "/js/scripts.js"), new JsHandler()),
                    new AbstractMap.SimpleEntry<HandlerMatcher, Handler>(new HandlerMatcher(HttpMethod.GET, "/css/styles.css"), new CssHandler()),
                    new AbstractMap.SimpleEntry<HandlerMatcher, Handler>(new HandlerMatcher(HttpMethod.GET, "/login"), new LoginHandler()),
                    new AbstractMap.SimpleEntry<HandlerMatcher, Handler>(new HandlerMatcher(HttpMethod.POST, "/login"), new LoginHandler()),
                    new AbstractMap.SimpleEntry<HandlerMatcher, Handler>(new HandlerMatcher(HttpMethod.GET, "/register"), new RegisterHandler()),
                    new AbstractMap.SimpleEntry<HandlerMatcher, Handler>(new HandlerMatcher(HttpMethod.POST, "/register"), new RegisterHandler())
            ));

    public static boolean canHandle(HandlerMatcher handlerMatcher) {
        if (handlerMap.containsKey(handlerMatcher)) {
            return true;
        }
        return false;
    }

    public static Handler getHandler(HandlerMatcher handlerMatcher) {
        return handlerMap.get(handlerMatcher);
    }

    public static Handler getExceptionHandler(HttpStatus httpStatus) {
        return new ExceptionHandler(httpStatus);
    }
}
