package com.techcourse.servlet;

import com.techcourse.controller.GreetingController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.controller.page.LoginPageController;
import com.techcourse.controller.page.RegisterPageController;
import com.techcourse.exception.UncheckedServletException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;

public class DispatcherServlet {

    private static final DispatcherServlet INSTANCE = new DispatcherServlet();

    private final List<HttpRequestHandler> handlers;

    private DispatcherServlet() {
        handlers = new ArrayList<>();
        handlers.add(new StaticResourceController());
        handlers.add(new GreetingController());
        handlers.add(new LoginPageController());
        handlers.add(new LoginController());
        handlers.add(new RegisterPageController());
        handlers.add(new RegisterController());
    }

    public static DispatcherServlet getInstance() {
        return INSTANCE;
    }

    public HttpRequestHandler mappedHandler(HttpServletRequest request) {
        return handlers.stream()
                .filter(httpRequestHandler -> httpRequestHandler.supports(request))
                .findFirst()
                .orElseThrow(() -> new UncheckedServletException("지원하지 않는 자원 요청입니다"));
    }

    public void doDispatch(HttpServletRequest request, HttpServletResponse response) {
        
    }
}
