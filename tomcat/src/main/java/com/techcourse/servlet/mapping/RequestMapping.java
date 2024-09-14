package com.techcourse.servlet.mapping;

import com.techcourse.servlet.DefaultServlet;
import com.techcourse.servlet.GreetingServlet;
import com.techcourse.servlet.LoginServlet;
import com.techcourse.servlet.RegisterServlet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.http.request.HttpRequest;

public class RequestMapping {

    private final Map<String, Servlet> handlers = new ConcurrentHashMap<>();

    public RequestMapping() {
        handlers.put("/", new GreetingServlet());
        handlers.put("/login", new LoginServlet());
        handlers.put("/register", new RegisterServlet());
    }

    public Servlet getServlet(HttpRequest httpRequest) {
        return handlers.getOrDefault(httpRequest.getUriPath(), new DefaultServlet());
    }
}
