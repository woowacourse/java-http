package org.apache.catalina.servlet;

import java.util.List;
import nextstep.jwp.exception.NotSupportedRequestException;
import nextstep.jwp.handler.DispatcherServlet;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;

public class ServletMapping {

    private static final List<HttpServlet> servlets = List.of(
            new DefaultServlet(),
            new DispatcherServlet()
    );

    public static HttpServlet getSupportedServlet(final HttpRequest request) {
        return servlets.stream().filter(it -> it.isSupported(request))
                .findFirst()
                .orElseThrow(NotSupportedRequestException::new);
    }
}
