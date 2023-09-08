package org.apache.catalina.controller;

import static org.apache.coyote.http11.common.HttpStatus.NOT_FOUND;
import static org.apache.coyote.http11.common.HttpStatus.OK;

import java.net.URL;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticController extends AbstractController {

    private static final String STATIC_RESOURCE_PREFIX = "static";
    private static final String NOT_FOUND_PAGE = "/404.html";

    private final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final RequestLine requestLine = request.getRequestLine();
        final String uri = requestLine.parseUri();

        final URL resource = classLoader.getResource(STATIC_RESOURCE_PREFIX + uri);
        if (resource == null) {
            response.setHttpStatus(NOT_FOUND).sendRedirect(NOT_FOUND_PAGE);
            return;
        }
        response.setHttpStatus(OK).sendRedirect(uri);
    }
}
