package nextstep.jwp.controller;

import org.apache.catalina.servlet.adapter.AbstractHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.net.URL;

import static org.apache.coyote.http11.response.HttpStatus.FOUND;
import static org.apache.coyote.http11.response.HttpStatus.NOT_FOUND;

public class StaticHandler extends AbstractHandler {

    private static final String STATIC_RESOURCE_PREFIX = "static";
    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String uri = request.getPath();
        final URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_PREFIX + uri + ".html");
        if (resource == null) {
            response.setHttpStatus(request.getHttpVersion(), NOT_FOUND);
            response.setRedirect(NOT_FOUND_PAGE);
            return;
        }
        response.setHttpStatus(request.getHttpVersion(), FOUND);
        response.setRedirect(uri + ".html");
    }
}