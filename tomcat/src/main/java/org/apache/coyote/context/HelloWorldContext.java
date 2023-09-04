package org.apache.coyote.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Container;
import org.apache.coyote.Handler;
import org.apache.coyote.context.exception.InvalidRootContextPathException;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.LoginPageHandler;
import org.apache.coyote.handler.ResourceHandler;
import org.apache.coyote.handler.WelcomeHandler;
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

    private final String rootContextPath;
    private final String staticResourcePath;
    private final List<Handler> handlers = new ArrayList<>();
    private final Handler resourceHandler;

    public HelloWorldContext(final String rootContextPath) {
        this(rootContextPath, DEFAULT_STATIC_RESOURCE_PATH_PREFIX);
    }

    public HelloWorldContext(final String rootContextPath, final String staticResourcePath) {
        if (rootContextPath == null || rootContextPath.isEmpty() || rootContextPath.isBlank()) {
            throw new InvalidRootContextPathException();
        }

        this.rootContextPath = rootContextPath;
        this.staticResourcePath = staticResourcePath;
        this.resourceHandler = new ResourceHandler(staticResourcePath);

        initHandlers();
    }

    private void initHandlers() {
        handlers.add(new WelcomeHandler(rootContextPath));
        handlers.add(new LoginHandler("/login", rootContextPath));
        handlers.add(new LoginPageHandler("/login", rootContextPath, "login.html", staticResourcePath));
    }

    @Override
    public boolean supports(final Request request) {
        return request.matchesByRootContextPath(rootContextPath);
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
            if (handler.supports(request)) {
                request.initSessionManager(SESSION_MANAGER);
                return handler.service(request);
            }
        }

        return processStaticResources(request);
    }

    private Response processStaticResources(final Request request) throws IOException {
        if (resourceHandler.supports(request)) {
            return resourceHandler.service(request);
        }

        return Response.of(request, HttpStatusCode.NOT_FOUND, ContentType.JSON, "존재하지 않는 api 입니다.");
    }
}
