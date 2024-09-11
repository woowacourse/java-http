package com.techcourse.servlet.mapping;

import static org.apache.coyote.http11.HttpProtocol.HTTP_11;
import static org.apache.coyote.http11.request.line.Method.GET;
import static org.apache.coyote.http11.request.line.Method.POST;

import com.techcourse.controller.GreetingController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.page.LoginPageController;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpServletRequest;

public class RequestMappingHandlerMapping implements HandlerMapping {

    private final Map<RequestMappingInformation, HttpRequestHandler> handlers = new ConcurrentHashMap<>();

    public RequestMappingHandlerMapping() {
        handlers.put(new RequestMappingInformation(GET, "/", HTTP_11), new GreetingController());
        handlers.put(new RequestMappingInformation(GET, "/login", HTTP_11), new LoginPageController());
        handlers.put(new RequestMappingInformation(POST, "/login", HTTP_11), new LoginController());
        handlers.put(new RequestMappingInformation(GET, "/register", HTTP_11), new RegisterController());
        handlers.put(new RequestMappingInformation(POST, "/register", HTTP_11), new LoginPageController());
    }

    @Override
    public Optional<HttpRequestHandler> getHandler(HttpServletRequest httpServletRequest) {
        return handlers.keySet().stream()
                .filter(mappingInformation -> mappingInformation.matches(httpServletRequest))
                .map(handlers::get)
                .findAny();
    }
}
