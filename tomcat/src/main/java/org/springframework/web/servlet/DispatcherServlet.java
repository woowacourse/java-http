package org.springframework.web.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.springframework.web.servlet.mvc.Controller;

import nextstep.jwp.controller.UserController;

public class DispatcherServlet {

    private static final DispatcherServlet DISPATCHER_SERVLET = new DispatcherServlet();

    private final HandlerMapping HANDLER_MAPPING = HandlerMapping.getInstance();
    private final ViewResolver VIEW_RESOLVER = ViewResolver.getInstance();

    private DispatcherServlet() {
        mapUrlToController();
        mapViewToUrl();
    }

    public static DispatcherServlet getInstance() {
        return DISPATCHER_SERVLET;
    }

    public HttpResponse doDispatch(final HttpRequest httpRequest) {
        final MappingResponse response = VIEW_RESOLVER.get(httpRequest.getUrl());
        final String resource = response.getResource();
        final String statusCode = response.getStatusCode();

        return HttpResponse.of(httpRequest.getHttpVersion(), resource, statusCode);
    }

    private void mapUrlToController() {
        final UserController controller = UserController.getInstance();

        map("/", controller);
        map("/login", controller);
    }

    private void mapViewToUrl() {
        map("/", new MappingResponse("/helloWorld.html", "OK"));
        map("/login", new MappingResponse("/login.html", "OK"));
    }

    private void map(final String url, final Controller controller) {
        HANDLER_MAPPING.add(url, controller);
    }

    private void map(final String url, final MappingResponse mappingResponse) {
        VIEW_RESOLVER.add(url, mappingResponse);
    }
}
