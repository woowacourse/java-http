package com.techcourse.servlet.mapping;

import com.techcourse.servlet.DefaultServlet;
import com.techcourse.servlet.GreetingServlet;
import com.techcourse.servlet.LoginServlet;
import com.techcourse.servlet.RegisterServlet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.http11.request.HttpServletRequest;

public class RequestMapping {

    private final Map<String, Servlet> handlers = new ConcurrentHashMap<>();

    public RequestMapping() {
        handlers.put("/", new GreetingServlet());
        handlers.put("/login", new LoginServlet());
        handlers.put("/register", new RegisterServlet());
    }

    public Servlet getServlet(HttpServletRequest httpServletRequest) {
        return handlers.getOrDefault(httpServletRequest.getUriPath(), new DefaultServlet());
    }
}
