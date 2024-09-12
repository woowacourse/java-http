package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.io.IOException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestParameter;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.MediaType;
import org.apache.coyote.http11.ResourceReader;
import org.apache.coyote.http11.SessionManager;

public class UserController implements Handler {
    private static final UserController INSTANCE = new UserController();

    private final ResourceReader resourceReader = ResourceReader.getInstance();

    private UserController() {
    }

    public static UserController getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponse doHandle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String path = httpRequest.getPath();
        if (path.startsWith("/register")) {
            return register(httpRequest, httpResponse);
        }
        if (path.startsWith("/login")) {
            return login(httpRequest, httpResponse);
        }
        return null;
    }

    public HttpResponse register(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpMethod httpRequestMethod = httpRequest.getHttpMethod();
        if (httpRequestMethod == HttpMethod.POST) {
            String redirectUrl = "/index.html";
            HttpRequestParameter requestParameter = httpRequest.getHttpRequestParameter();
            try {
                UserService.createUser(requestParameter);
            } catch (IllegalArgumentException e) {
                redirectUrl = "/400.html";
            }
            httpResponse.addHttpStatusCode(HttpStatusCode.FOUND)
                    .addRedirectUrl(redirectUrl);
        } else if (httpRequestMethod == HttpMethod.GET) {
            String responseBody = resourceReader.loadResourceAsString("register.html");
            httpResponse.addContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                    .addHttpStatusCode(HttpStatusCode.OK)
                    .addResponseBody(responseBody);
        }
        return httpResponse;
    }

    public HttpResponse login(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpMethod httpRequestMethod = httpRequest.getHttpMethod();
        if (httpRequestMethod == HttpMethod.POST) {
            String redirectUrl = "/index.html";
            HttpRequestParameter requestParameter = httpRequest.getHttpRequestParameter();
            try {
                String sessionId = UserService.login(requestParameter);
                httpResponse.addHttpStatusCode(HttpStatusCode.FOUND)
                        .addCookie("JSESSIONID", sessionId)
                        .addCookie("Max-Age", "600")
                        .addRedirectUrl(redirectUrl);
            } catch (IllegalArgumentException e) {
                httpResponse.addHttpStatusCode(HttpStatusCode.FOUND)
                        .addRedirectUrl("/401.html");
            }
        } else if (httpRequestMethod == HttpMethod.GET && !validateSession(httpRequest.getSessionId())) {
            String responseBody = resourceReader.loadResourceAsString("login.html");
            httpResponse.addContentType(new ContentType(MediaType.HTML, "charset=utf-8"))
                    .addHttpStatusCode(HttpStatusCode.OK)
                    .addResponseBody(responseBody);
        } else if (httpRequestMethod == HttpMethod.GET && validateSession(httpRequest.getSessionId())) {
            httpResponse.addHttpStatusCode(HttpStatusCode.FOUND)
                    .addRedirectUrl("/index.html");
        }
        return httpResponse;
    }

    private boolean validateSession(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        return SessionManager.findSession(sessionId) != null;
    }
}
