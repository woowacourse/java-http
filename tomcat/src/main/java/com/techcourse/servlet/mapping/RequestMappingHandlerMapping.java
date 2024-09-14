package com.techcourse.servlet.mapping;

import static org.apache.coyote.http11.common.HttpProtocol.HTTP_11;
import static org.apache.coyote.http11.request.line.Method.GET;
import static org.apache.coyote.http11.request.line.Method.POST;

import com.techcourse.servlet.GreetingServlet;
import com.techcourse.servlet.LoginPageServlet;
import com.techcourse.servlet.LoginServlet;
import com.techcourse.servlet.RegisterPageServlet;
import com.techcourse.servlet.RegisterServlet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.servlet.Servlet;
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
        RequestMappingInformation mappingKey = RequestMappingInformation.from(httpServletRequest);
        return handlers.containsKey(mappingKey);
    }

    @Override
    public Servlet getHandler(HttpServletRequest httpServletRequest) {
        RequestMappingInformation mappingKey = RequestMappingInformation.from(httpServletRequest);
        return handlers.get(mappingKey);
    }
}
