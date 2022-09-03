package org.apache.servlet;

import java.util.List;
import nextstep.jwp.controller.IndexServlet;
import nextstep.jwp.controller.LoginServlet;

public class ServletContainer {

    private static final List<Servlet> SERVLETS = List.of(new IndexServlet(), new LoginServlet());
    private static final Servlet NOT_FOUND_SERVLET = new NotFoundServlet();

    public Servlet findByPath(final String path) {
        return SERVLETS.stream()
                .filter(servlet -> servlet.isSupported(path))
                .findFirst()
                .orElse(NOT_FOUND_SERVLET);
    }
}
