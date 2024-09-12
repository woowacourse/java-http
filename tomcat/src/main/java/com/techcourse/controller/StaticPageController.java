package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class StaticPageController {

    private static final StaticPageController instance = new StaticPageController();

    private StaticPageController() {
    }

    public void getStaticPage(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        FileReader fileReader = FileReader.getInstance();
        response.setHttpStatusCode(HttpStatusCode.OK);
        if (request.getHttpRequestPath().equals("/login") && checkLogin(request)) {
            response.setHttpStatusCode(HttpStatusCode.FOUND);
            request.setHttpRequestPath("/index.html");
        }

        response.setHttpResponseBody(fileReader.readFile(request.getHttpRequestPath()));
        response.setHttpResponseHeader("Content-Type", request.getContentType() + ";charset=utf-8");
        response.setHttpResponseHeader("Content-Length", String.valueOf(response.getHttpResponseBody().body().getBytes().length));
    }

    private boolean checkLogin(HttpRequest request) {
        String jsessionid = request.getJSESSIONID();
        if (jsessionid.isEmpty()) {
            return false;
        }
        return SessionManager.containsSession(jsessionid);
    }

    public static StaticPageController getInstance() {
        return instance;
    }
}
