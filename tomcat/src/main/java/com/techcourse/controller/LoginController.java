package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.data.ContentType;
import org.apache.coyote.http11.data.HttpCookie;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpRequestParameter;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.http11.data.MediaType;
import org.apache.coyote.http11.resource.ResourceReader;
import org.apache.coyote.http11.session.SessionManager;

public class LoginController extends AbstractController {
    private static final LoginController INSTANCE = new LoginController();

    private final ResourceReader resourceReader = ResourceReader.getInstance();

    private LoginController() {
    }

    public static LoginController getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        HttpRequestParameter requestParameter = request.getHttpRequestParameter();
        try {
            String sessionId = UserService.login(requestParameter);
            HttpCookie httpCookie = new HttpCookie("JSESSIONID", sessionId, Map.of("Max-Age", "600"));
            response.addCookie(httpCookie);
            response.addHttpStatusCode(HttpStatusCode.FOUND)
                    .addCookie(httpCookie)
                    .addRedirectUrl("/index.html");
        } catch (IllegalArgumentException e) {
            response.addHttpStatusCode(HttpStatusCode.FOUND)
                    .addRedirectUrl("/401.html");
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (validateSession(request.getSessionId())) {
            response.addHttpStatusCode(HttpStatusCode.FOUND)
                    .addRedirectUrl("/index.html");
            return;
        }
        String responseBody = resourceReader.loadResourceAsString("login.html");
        response.addContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                .addHttpStatusCode(HttpStatusCode.OK)
                .addResponseBody(responseBody);
    }

    private boolean validateSession(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        return SessionManager.findSession(sessionId) != null;
    }
}
