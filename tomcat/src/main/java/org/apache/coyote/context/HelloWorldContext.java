package org.apache.coyote.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Context;
import org.apache.coyote.Handler;
import org.apache.coyote.context.exception.InvalidRootContextPathException;
import org.apache.coyote.context.exception.InvalidStaticResourcePathException;
import org.apache.coyote.context.exception.UnsupportedApiException;
import org.apache.coyote.http.SessionManager;
import servlet.request.HttpRequest;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpHeaderConsts;
import org.apache.coyote.http.util.ResponseGenerator;
import org.apache.coyote.http.util.exception.UnsupportedHttpMethodException;
import servlet.Controller;
import servlet.response.HttpResponse;

public class HelloWorldContext implements Context {

    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final String DEFAULT_STATIC_RESOURCE_PATH_PREFIX = "static/";

    private final String contextPath;
    private final String staticResourcePath;
    private final List<Handler> handlers = new ArrayList<>();
    private final Controller controller;

    public HelloWorldContext(final String contextPath, final Controller controller) {
        this(contextPath, DEFAULT_STATIC_RESOURCE_PATH_PREFIX, controller);
    }

    public HelloWorldContext(final String contextPath, final String staticResourcePath, final Controller controller) {
        validateRootContextPath(contextPath);
        validatePath(staticResourcePath);

        this.contextPath = contextPath;
        this.staticResourcePath = staticResourcePath;
        this.controller = controller;
    }

    private void validateRootContextPath(final String contextPath) {
        if (contextPath == null || !contextPath.startsWith(HttpConsts.SLASH)) {
            throw new InvalidRootContextPathException();
        }
    }

    private void validatePath(final String staticResourcePath) {
        if (staticResourcePath == null || !staticResourcePath.endsWith(HttpConsts.SLASH)) {
            throw new InvalidStaticResourcePathException();
        }
    }

    @Override
    public boolean supports(final Request request) {
        return request.matchesByContextPath(contextPath);
    }

    @Override
    public Response service(final Request request) throws IOException {
        try {
            return process(request);
        } catch (final UnsupportedHttpMethodException e) {
            return Response.of(
                    request,
                    HttpStatusCode.METHOD_NOT_ALLOWED,
                    ContentType.TEXT_HTML,
                    HttpConsts.BLANK,
                    new HeaderDto(HttpHeaderConsts.LOCATION, "/406.html")
            );
        } catch (final UnsupportedApiException e) {
            return Response.of(
                    request,
                    HttpStatusCode.NOT_FOUND,
                    ContentType.TEXT_HTML,
                    HttpConsts.BLANK,
                    new HeaderDto(HttpHeaderConsts.LOCATION, "/404.html")
            );
        }
    }

    private Response process(final Request request) throws IOException {
        final HttpRequest httpRequest = new HttpRequest(request, contextPath, SESSION_MANAGER);
        final HttpResponse httpResponse = new HttpResponse();

        if (httpRequest.isBusinessLogic(contextPath)) {
            try {
                controller.service(httpRequest, httpResponse);

                return ResponseGenerator.generate(httpRequest, httpResponse);
            } catch (final Exception e) {
                return Response.of(
                        request,
                        HttpStatusCode.FOUND,
                        ContentType.TEXT_HTML,
                        HttpConsts.BLANK,
                        new HeaderDto(HttpHeaderConsts.LOCATION, "/500.html")
                );
            }
        }

        for (final Handler handler : handlers) {
            if (handler.supports(request, contextPath)) {
                return handler.service(request, staticResourcePath);
            }
        }

        throw new UnsupportedApiException();
    }

    @Override
    public void addHandler(final Handler handler) {
        this.handlers.add(handler);
    }
}
