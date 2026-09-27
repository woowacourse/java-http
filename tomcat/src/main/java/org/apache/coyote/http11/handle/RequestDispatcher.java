package org.apache.coyote.http11.handle;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.filter.FilterChainFactory;

public class RequestDispatcher implements RequestHandler {
    private final Controller[] controllers;
    private final FilterChainFactory filterChainFactory;
    private final ViewResolver viewResolver;

    public static RequestDispatcher create(
            FilterChainFactory filterChainFactory,
            ViewResolver viewResolver,
            AbstractController... handlers) {
        return new RequestDispatcher(filterChainFactory, viewResolver, handlers);
    }

    private RequestDispatcher(
            FilterChainFactory filterChainFactory,
            ViewResolver viewResolver,
            AbstractController... controllers) {
        this.controllers = controllers;
        this.filterChainFactory = filterChainFactory;
        this.viewResolver = viewResolver;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        filterChainFactory.create(this::handleController).doFilter(request, response);
        viewResolver.resolve(response);
    }

    private void handleController(HttpRequest request, HttpResponse response) {
        for (Controller controller : controllers) {
            if (controller.canHandle(request)) {
                controller.service(request, response);
                return;
            }
        }

        response.notFound();
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return true;
    }
}
