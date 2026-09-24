package org.apache.catalina.connector;

import com.techcourse.api.Controller;
import com.techcourse.api.RequestMapping;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.StaticResourceLoader;
import org.apache.coyote.HttpHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public final class CatalinaHttpHandler implements HttpHandler {

    private final Manager sessionManager;
    private final RequestMapping requestMapping;

    public CatalinaHttpHandler(
            final Manager sessionManager,
            final RequestMapping requestMapping
    ) {
        this.sessionManager = sessionManager;
        this.requestMapping = requestMapping;
    }

    @Override
    public void handle(final HttpRequest request, final HttpResponse response) throws Exception {
        dispatch(request, response);
        addSessionCookieIfNecessary(request, response);
    }

    private void dispatch(final HttpRequest request, final HttpResponse response) throws Exception {
        final Controller controller = requestMapping.getController(request.getMethod(), request.getPath());
        if (controller != null) {
            controller.service(request, response);
            return;
        }

        if (request.isGet()) {
            response.setStatus(HttpStatus.OK);
            response.setContentType(ContentType.fromResourceName(request.getPath()));
            response.setBody(StaticResourceLoader.read(request.getPath()));
            return;
        }

        throw new UnsupportedOperationException("지원하지 않는 요청입니다: " + request.getMethod() + " " + request.getPath());
    }

    private void addSessionCookieIfNecessary(final HttpRequest request, final HttpResponse response) {
        if (request.getSessionId() != null || response.containsHeader("Set-Cookie")) {
            return;
        }

        final Session session = sessionManager.createSession(null);
        response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId());
    }
}
