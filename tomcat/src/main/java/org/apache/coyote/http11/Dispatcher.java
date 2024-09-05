package org.apache.coyote.http11;

import com.techcourse.controller.GreetingController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.controller.page.LoginPageController;
import com.techcourse.controller.page.RegisterPageController;
import com.techcourse.exception.UncheckedServletException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;

public class Dispatcher {

    private static final Dispatcher INSTANCE = new Dispatcher();

    private final List<HttpRequestHandler> handlers;

    private Dispatcher() {
        handlers = new ArrayList<>();
        handlers.add(new StaticResourceController());
        handlers.add(new GreetingController());
        handlers.add(new LoginPageController());
        handlers.add(new LoginController());
        handlers.add(new RegisterPageController());
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
