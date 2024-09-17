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
import org.apache.catalina.session.SessionManager;

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
            HttpCookie httpCookie = new HttpCookie(HttpRequest.SESSION_ID_COOKIE_KEY, sessionId, Map.of("Max-Age", "600"));
            response.setHttpStatusCode(HttpStatusCode.FOUND)
                    .addCookie(httpCookie)
                    .setRedirectUrl("/index.html");
        } catch (IllegalArgumentException e) {
            response.setHttpStatusCode(HttpStatusCode.FOUND)
                    .setRedirectUrl("/401.html");
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (validateSession(request.getSessionId())) {
            response.setHttpStatusCode(HttpStatusCode.FOUND)
                    .setRedirectUrl("/index.html");
            return;
        }
        String responseBody = resourceReader.loadResourceAsString("login.html");
        response.setContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                .setHttpStatusCode(HttpStatusCode.OK)
                .setResponseBody(responseBody);
    }

    private boolean validateSession(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        return SessionManager.findSession(sessionId) != null;
    }
}
