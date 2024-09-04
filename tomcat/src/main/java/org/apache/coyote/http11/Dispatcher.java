package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.handler.GreetingHandler;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.handler.LoginPageHandler;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.apache.coyote.http11.request.HttpRequest;

public class Dispatcher {

    private static final Dispatcher INSTANCE = new Dispatcher();

    private final List<HttpRequestHandler> handlers;

    private Dispatcher() {
        handlers = new ArrayList<>();
        handlers.add(new StaticResourceHandler());
        handlers.add(new GreetingHandler());
        handlers.add(new LoginPageHandler());
    }

    public static Dispatcher getInstance() {
        return INSTANCE;
    }

    public HttpRequestHandler mappedHandler(HttpRequest request) {
        return handlers.stream()
                .filter(httpRequestHandler -> httpRequestHandler.supports(request))
                .findAny()
                .orElseThrow(() -> new UncheckedServletException("지원하지 않는 자원 요청입니다"));
    }
}
