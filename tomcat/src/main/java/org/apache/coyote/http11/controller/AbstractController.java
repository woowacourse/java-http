package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.Session.SessionManager;
import org.apache.coyote.http11.Request.HttpRequest;
import org.apache.coyote.http11.Response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    protected static final SessionManager SESSION_MANAGER = new SessionManager();
    protected static final Logger log = LoggerFactory.getLogger(Controller.class);

    @Override
    public HttpResponse service(final HttpRequest request) throws IOException {
        final String method = request.getRequestLine().getMethod();
        if (method.equals("GET")) {
            return doGet(request);
        }
        if (method.equals("POST")) {
            return doPost(request);
        }
        return HttpResponse.methodNotAllowed();
    }

    protected abstract HttpResponse doPost(HttpRequest request) throws IOException;

    protected abstract HttpResponse doGet(HttpRequest request) throws IOException;

    protected String getContentType(final String path) throws IOException {
        return Files.probeContentType(Path.of(path));
    }
}
