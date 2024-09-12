package com.techcourse.servlet.mapping;

import static org.apache.coyote.http11.HttpProtocol.HTTP_11;
import static org.apache.coyote.http11.request.line.Method.GET;
import static org.apache.coyote.http11.request.line.Method.POST;

import com.techcourse.controller.GreetingServlet;
import com.techcourse.controller.LoginServlet;
import com.techcourse.controller.RegisterServlet;
import com.techcourse.controller.page.LoginPageServlet;
import com.techcourse.controller.page.RegisterPageServlet;
import com.techcourse.exception.UncheckedServletException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.Servlet;
import org.apache.coyote.http11.request.HttpServletRequest;

public class RequestMappingHandlerMapping implements HandlerMapping {

    private final Map<RequestMappingInformation, Servlet> handlers = new ConcurrentHashMap<>();

    public RequestMappingHandlerMapping() {
        handlers.put(new RequestMappingInformation(GET, "/", HTTP_11), new GreetingServlet());
        handlers.put(new RequestMappingInformation(GET, "/login", HTTP_11), new LoginPageServlet());
        handlers.put(new RequestMappingInformation(POST, "/login", HTTP_11), new LoginServlet());
        handlers.put(new RequestMappingInformation(GET, "/register", HTTP_11), new RegisterPageServlet());
        handlers.put(new RequestMappingInformation(POST, "/register", HTTP_11), new RegisterServlet());
    }

    @Override
    public boolean hasHandlerFor(HttpServletRequest httpServletRequest) {
        RequestMappingInformation requestMappingInformation = RequestMappingInformation.from(httpServletRequest);
        return handlers.containsKey(requestMappingInformation);
    }

    @Override
    public Servlet getHandler(HttpServletRequest httpServletRequest) {
        return handlers.keySet().stream()
                .filter(mappingInformation -> mappingInformation.matches(httpServletRequest))
                .map(handlers::get)
                .findAny()
                .orElseThrow(() -> new UncheckedServletException("요청을 처리할 수 있는 핸들러가 없습니다"));
    }
}
