package org.apache.coyote.http11.http11handler.impl;

import java.io.IOException;
import nextstep.jwp.model.user.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.http11handler.login.LoginService;
import org.apache.coyote.http11.http11response.ResponseComponent;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11request.Http11Request;

public class LoginPageHandler implements Http11Handler {

    private static final String URI = "/login";
    private static final String URI_WITH_EXTENSION = "/login.html";
    private static final String REDIRECT_URI_ALREADY_LOGIN = "/index.html";
    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;

    private HandlerSupporter handlerSupporter = new HandlerSupporter();
    private SessionManager sessionManager = SessionManager.of();
    private LoginService loginService = new LoginService();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        return URI.equals(http11Request.getUri()) && http11Request.getHttpMethod().equals(HTTP_METHOD);
    }

    @Override
    public ResponseComponent handle(Http11Request http11Request) {
        if (alreadyLogin(http11Request)) {
            return handlerSupporter.redirectResponseComponent(REDIRECT_URI_ALREADY_LOGIN, StatusCode.REDIRECT);
        }
        return handlerSupporter.resourceResponseComponent(URI_WITH_EXTENSION, StatusCode.OK);
    }

    private boolean alreadyLogin(Http11Request http11Request) {
        if (!http11Request.hasCookie()) {
            return false;
        }
        String cookie = http11Request.getCookie();
        HttpCookie httpCookie = HttpCookie.of(cookie);
        if (!httpCookie.hasJessionId()) {
            return false;
        }
        String jsessionId = httpCookie.getJsessionId();
        try {
            Session session = sessionManager.findSession(jsessionId);
            User user = (User)session.getAttribute("user");
            return loginService.isExistUser(user);
        } catch (IOException e) {
            return false;
        }
    }

}
