package org.apache.catalina.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletContainer {

    private static final List<Servlet> DEFAULT_SERVLETS = List.of(new StaticResourcesServlet());
    private static final Logger log = LoggerFactory.getLogger(ServletContainer.class);

    private final List<Servlet> servlets;

    private ServletContainer(List<Servlet> servlets) {
        this.servlets = servlets;
    }

    public static ServletContainer init(List<Servlet> servlets) {
        List<Servlet> newServlets = new ArrayList<>(DEFAULT_SERVLETS);
        newServlets.addAll(servlets);

        return new ServletContainer(newServlets);
    }

    public void service(HttpRequest request, HttpResponse response) throws IOException {
        try {
            Optional<Servlet> matchingServlet = findMatchingServlet(request);
            if (matchingServlet.isPresent()) {
                log.debug("Request Line: {}", request.getRequestLine());
                log.debug("Body: {}", request.getParams());

                matchingServlet.get().service(request, response);
                return;
            }

            responseNotFoundPage(response);
        } catch (Exception e) {
            log.error("Internal server error", e);
            responseInternalServerErrorPage(response);
        }
    }

    private Optional<Servlet> findMatchingServlet(HttpRequest request) {
        return servlets.stream()
                .filter(servlet -> servlet.canService(request))
                .findFirst();
    }

    private void responseNotFoundPage(HttpResponse response) throws IOException {
        response.setHttpStatus(HttpStatus.NOT_FOUND);
        response.setStaticResourceResponse("/404.html");
        response.write();
    }

    private void responseInternalServerErrorPage(HttpResponse response) throws IOException {
        response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setStaticResourceResponse("/500.html");
        response.write();
    }
}
