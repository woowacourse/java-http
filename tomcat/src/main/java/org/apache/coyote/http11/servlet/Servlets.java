package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestURI;
import org.apache.coyote.http11.message.response.HttpResponse;

public class Servlets {

    private static final Map<String, Servlet> mappings;

    static {
        mappings = new HashMap<>();
        mappings.put("/", new RootServlet());
        mappings.put("/index", new IndexServlet());
        mappings.put("/login", new LoginServlet());
        mappings.put("/register", new RegisterServlet());
    }

    private Servlets() {
    }

    public static HttpResponse service(HttpRequest httpRequest) throws IOException {
        RequestURI requestURI = httpRequest.getRequestURI();
        String absolutePath = requestURI.absolutePath();

        Servlet servlet = mappings.entrySet().stream()
                .filter(entry -> entry.getKey().equals(absolutePath))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseGet(StaticResourceServlet::new);

        return servlet.service(httpRequest);
    }
}
