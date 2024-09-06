package com.techcourse.controller.page;

import java.io.IOException;
import org.apache.coyote.http11.HttpProtocol;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.request.line.Uri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.util.FileUtils;

public class LoginPageController implements HttpRequestHandler {

    private static final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String PAGE_RESOURCE_PATH = "/login.html";
    private static final Method SUPPORTING_METHOD = Method.GET;
    private static final Uri SUPPORTING_URI = new Uri("/login");
    private static final HttpProtocol SUPPORTING_PROTOCOL = HttpProtocol.HTTP_11;
    private static final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public boolean supports(final HttpRequest request) {
        if (request.methodNotEqual(SUPPORTING_METHOD)) {
            return false;
        }
        if (request.protocolNotEqual(SUPPORTING_PROTOCOL)) {
            return false;
        }
        if (request.uriNotEqual(SUPPORTING_URI)) {
            return false;
        }
        return true;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        if (sessionManager.hasSession(request.getSessionId())) {
            return HttpResponse.redirect(LOGIN_SUCCESS_REDIRECT_URI);
        }

        String fileContent = FileUtils.readFile(PAGE_RESOURCE_PATH);
        return HttpResponse.ok(fileContent, "html");
    }
}
