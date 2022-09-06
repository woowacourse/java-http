package org.apache.servlet;

import java.util.List;
import nextstep.jwp.controller.IndexServlet;
import nextstep.jwp.controller.LoginServlet;
import nextstep.jwp.controller.RegisterServlet;

public class ServletContainer {

    private static final List<Servlet> SERVLETS = List.of(new IndexServlet(), new LoginServlet(), new RegisterServlet(),
            new ResourceServlet());

    private static final Servlet NOT_FOUND_SERVLET = new NotFoundServlet();

    private ServletContainer() {
    }

    public static Servlet findByPath(final String path) {
        return SERVLETS.stream()
                .filter(servlet -> servlet.isSupported(path))
                .findFirst()
                .orElse(NOT_FOUND_SERVLET);
    }
}
