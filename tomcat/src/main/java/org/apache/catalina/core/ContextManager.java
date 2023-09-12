package org.apache.catalina.core;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.servlet.DispatcherServlet;
import org.apache.coyote.exception.NotFoundServletException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class ContextManager {

    private static final List<Servlet> servlets = new ArrayList<>();

    static {
        servlets.add(new DispatcherServlet());
    }

    public static void invoke(final HttpRequest request, final HttpResponse response) throws Exception {
        final Servlet servlet = servlets.stream()
                .findFirst()
                .orElseThrow(NotFoundServletException::new);
        servlet.service(request, response);
    }
}
