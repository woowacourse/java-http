package org.apache.coyote.http11.controller;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatus;

import java.net.URL;

public class LoginGetController extends AbstractController {
    public static final String JSESSIONID = "JSESSIONID";

    @Override
    public boolean isSupported(HttpRequest request) {
        return request.isGET() && request.isSamePath("/login");
    }


    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        final HttpCookie cookie = request.getCookie();
        URL filePathUrl;
        if (isLogin(cookie)) {
            filePathUrl = getClass().getResource("/static/index.html");
        } else {
            filePathUrl = getClass().getResource("/static/login.html");
        }
        String responseBody = getHtmlFile(filePathUrl);

        HttpResponseHeader responseHeader = new HttpResponseHeader(
                getContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.OK, responseHeader, responseBody);
    }

    private boolean isLogin(HttpCookie cookie) {
        return cookie.isExist(JSESSIONID)
                && SessionManager.InstanceOf().findSession(cookie.findCookie(JSESSIONID)) != null;
    }
}
