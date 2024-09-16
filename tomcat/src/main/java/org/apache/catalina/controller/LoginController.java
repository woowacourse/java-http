package org.apache.catalina.controller;

import org.apache.catalina.ResourceManager;
import org.apache.catalina.AuthManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import java.io.IOException;

import static org.apache.catalina.AuthManager.AUTHENTICATION_COOKIE_NAME;
import static org.apache.coyote.http11.Status.FOUND;
import static org.apache.coyote.http11.Status.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.Status.OK;

public class LoginController extends AbstractController {

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            Session session = AuthManager.authenticate(httpRequest);

            httpResponse.setStatusLine(FOUND);
            httpResponse.setCookie(AUTHENTICATION_COOKIE_NAME, session.getId());
            httpResponse.setLocation("/index.html");
        } catch (IllegalArgumentException exception) {
            httpResponse.setStatusLine(FOUND);
            httpResponse.setLocation("/401.html");
        }
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            if (AuthManager.isAuthenticated(httpRequest)) {
                Session session = AuthManager.getAuthenticatedSession(httpRequest);

                httpResponse.setStatusLine(FOUND);
                httpResponse.setCookie(AUTHENTICATION_COOKIE_NAME, session.getId());
                httpResponse.setLocation("/index.html");

                return;
            }

            String responseBody = ResourceManager.getFileResource(httpRequest.getPath());

            httpResponse.setStatusLine(OK);
            httpResponse.setContentType(httpRequest.getContentType());
            httpResponse.setResponseBody(responseBody);
            httpResponse.setContentLength(responseBody.getBytes().length);
        } catch (IllegalArgumentException exception) {
            httpResponse.setStatusLine(FOUND);
            httpResponse.setLocation("/401.html");
        } catch (IOException exception) {
            httpResponse.setStatusLine(INTERNAL_SERVER_ERROR);
        }
    }
}
