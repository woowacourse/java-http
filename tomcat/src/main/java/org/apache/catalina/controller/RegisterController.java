package org.apache.catalina.controller;

import org.apache.catalina.AuthManager;
import org.apache.catalina.ResourceManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import java.io.IOException;

import static org.apache.catalina.AuthManager.AUTHENTICATION_COOKIE_NAME;
import static org.apache.coyote.http11.Status.FOUND;
import static org.apache.coyote.http11.Status.OK;

public class RegisterController extends AbstractController {

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Session session = AuthManager.register(httpRequest);

        httpResponse.setStatusLine(FOUND);
        httpResponse.setCookie(AUTHENTICATION_COOKIE_NAME, session.getId());
        httpResponse.setLocation("/index.html");
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String responseBody = ResourceManager.getFileResource(httpRequest.getPath());

        httpResponse.setStatusLine(OK);
        httpResponse.setContentType(httpRequest.getContentType());
        httpResponse.setResponseBody(responseBody);
        httpResponse.setContentLength(responseBody.getBytes().length);
    }
}
