package org.apache.coyote.handlermapping;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.dynamichandler.LoginHandler;
import org.apache.coyote.handler.dynamichandler.RegisterHandler;
import org.apache.coyote.handler.statichandler.CssHandler;
import org.apache.coyote.handler.statichandler.HelloHandler;
import org.apache.coyote.handler.statichandler.HtmlHandler;
import org.apache.coyote.handler.statichandler.JsHandler;
import org.apache.coyote.http11.HttpMethod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMapping {

    private static Map<HandlerMatcher, Handler> handlerMap;

    public static boolean canHandle(HandlerMatcher handlerMatcher) {
        if (handlerMap == null) {
            init();
        }

        System.out.println(handlerMatcher.getHttpMethod() + " " + handlerMatcher.getRequestTarget() + "  " + handlerMap.containsKey(handlerMatcher));
        if (handlerMap.containsKey(handlerMatcher)) {
            return true;
        }
        return false;
    }

    public static void init() {
        handlerMap = new ConcurrentHashMap<>();

        // HelloHandler
        handlerMap.put(new HandlerMatcher(HttpMethod.GET, "/"), new HelloHandler());
        // HTML
        handlerMap.put(new HandlerMatcher(HttpMethod.GET, "/index.html"), new HtmlHandler());

        // JS
        handlerMap.put(new HandlerMatcher(HttpMethod.GET, "/assets/chart-area.js"), new JsHandler());
        handlerMap.put(new HandlerMatcher(HttpMethod.GET, "/assets/chart-bar.js"), new JsHandler());
        handlerMap.put(new HandlerMatcher(HttpMethod.GET, "/assets/chart-pie.js"), new JsHandler());
        handlerMap.put(new HandlerMatcher(HttpMethod.GET, "/js/scripts.js"), new JsHandler());

        // CSS
        handlerMap.put(new HandlerMatcher(HttpMethod.GET, "/css/styles.css"), new CssHandler());

        // Dynamic
        handlerMap.put(new HandlerMatcher(HttpMethod.GET, "/login"), new LoginHandler());
        handlerMap.put(new HandlerMatcher(HttpMethod.POST, "/login"), new LoginHandler());

        handlerMap.put(new HandlerMatcher(HttpMethod.GET, "/register"), new RegisterHandler());
        handlerMap.put(new HandlerMatcher(HttpMethod.POST, "/register"), new RegisterHandler());
    }

    public static Handler getHandler(HandlerMatcher handlerMatcher) {
        return handlerMap.get(handlerMatcher);
    }
}
