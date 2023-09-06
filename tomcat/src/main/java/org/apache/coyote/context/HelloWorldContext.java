package org.apache.coyote.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Container;
import org.apache.coyote.Handler;
import org.apache.coyote.context.exception.InvalidRootContextPathException;
import org.apache.coyote.context.exception.InvalidStaticResourcePathException;
import org.apache.coyote.context.exception.UnsupportedApiException;
import nextstep.handler.ResourceHandler;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.exception.UnsupportedHttpMethodException;

public class HelloWorldContext implements Container {

    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final String DEFAULT_STATIC_RESOURCE_PATH_PREFIX = "static/";

    private final String contextPath;
    private final String staticResourcePath;
    private final List<Handler> handlers = new ArrayList<>();
    private final Handler resourceHandler;

    public HelloWorldContext(final String contextPath) {
        this(contextPath, DEFAULT_STATIC_RESOURCE_PATH_PREFIX);
    }

    public HelloWorldContext(final String contextPath, final String staticResourcePath) {
        this(contextPath, staticResourcePath, new ResourceHandler());
    }

    public HelloWorldContext(
            final String contextPath,
            final String staticResourcePath,
            final Handler resourceHandler
    ) {
        validateRootContextPath(contextPath);
        validatePath(staticResourcePath);

        this.contextPath = contextPath;
        this.staticResourcePath = staticResourcePath;
        this.resourceHandler = resourceHandler;
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
        return request.matchesByRootContextPath(contextPath);
    }

    @Override
    public Response service(final Request request) throws IOException {
        try {
            return process(request);
        } catch (UnsupportedHttpMethodException ex) {
            return Response.of(request, HttpStatusCode.METHOD_NOT_ALLOWED, ContentType.JSON, HttpConsts.BLANK);
        }
    }

    private Response process(final Request request) throws IOException {
        for (final Handler handler : handlers) {
            if (handler.supports(request, contextPath)) {
                request.initSessionManager(SESSION_MANAGER);
                return handler.service(request, staticResourcePath);
            }
        }

        return processStaticResources(request);
    }

    private Response processStaticResources(final Request request) throws IOException {
        if (resourceHandler.supports(request, contextPath)) {
            return resourceHandler.service(request, staticResourcePath);
        }

        throw new UnsupportedApiException();
    }

    @Override
    public void addHandler(final Handler handler) {
        this.handlers.add(handler);
    }
}
