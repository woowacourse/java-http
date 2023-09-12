package org.apache.catalina.servlet;

import java.util.List;
import nextstep.jwp.handler.DispatcherServlet;
import org.apache.catalina.exception.NotSupportedRequestException;
import org.apache.coyote.http.vo.HttpRequest;

public class ServletMapping {

    private static final List<Controller> servlets = List.of(
            new DefaultServlet(),
            new DispatcherServlet()
    );

    private ServletMapping() {
    }

    public static Controller getSupportedServlet(final HttpRequest request) {
        return servlets.stream()
                .filter(it -> it.isSupported(request))
                .findFirst()
                .orElseThrow(NotSupportedRequestException::new);
    }
}
